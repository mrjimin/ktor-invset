package io.github.mrjimin.invest.domain.trading.service

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.common.time.TimeProvider
import io.github.mrjimin.invest.common.time.TradingCalendar
import io.github.mrjimin.invest.domain.account.model.Account
import io.github.mrjimin.invest.domain.account.repository.AccountRepository
import io.github.mrjimin.invest.domain.stock.repository.StockRepository
import io.github.mrjimin.invest.domain.trading.model.Order
import io.github.mrjimin.invest.domain.trading.model.OrderSide
import io.github.mrjimin.invest.domain.trading.model.OrderStatus
import io.github.mrjimin.invest.domain.trading.model.TaxCollection
import io.github.mrjimin.invest.domain.trading.model.Transaction
import io.github.mrjimin.invest.domain.trading.repository.HoldingRepository
import io.github.mrjimin.invest.domain.trading.repository.OrderRepository
import io.github.mrjimin.invest.domain.trading.repository.TaxRepository
import io.github.mrjimin.invest.domain.trading.repository.TransactionRepository
import kotlinx.datetime.LocalDateTime

internal class PendingOrderProcessor(
    private val accountRepository: AccountRepository,
    private val stockRepository: StockRepository,
    private val orderRepository: OrderRepository,
    private val transactionRepository: TransactionRepository,
    private val taxRepository: TaxRepository,
    private val timeProvider: TimeProvider,
    private val tradingCalendar: TradingCalendar,
    private val holdingProcessor: HoldingProcessor,
    private val holdingRepository: HoldingRepository,
    private val tradingFeeCalculator: TradingFeeCalculator
) {

    fun executePendingOrders(): Int {
        if (!tradingCalendar.isMarketOpen()) return 0

        val pendingOrderIds = withTransaction {
            orderRepository.findPendingOrderIds()
        }

        return pendingOrderIds.count { orderId ->
            withTransaction {
                executeOrder(
                    orderId = orderId,
                    now = timeProvider.nowUtc()
                )
            }
        }
    }

    private fun executeOrder(
        orderId: Long,
        now: LocalDateTime
    ): Boolean {

        val order = orderRepository.findByIdForUpdate(orderId)
            ?: return false

        if (order.status != OrderStatus.PENDING) return false

        val account = accountRepository.findByUserIdForUpdate(order.userId)
            ?: error("주문 계좌를 찾을 수 없습니다. orderId=${order.id}")

        val stock = stockRepository.findById(order.stockId)

        if (stock == null || !stock.listed) {
            rejectOrder(order, account, now)
            return false
        }

        val price = stock.currentPrice

        val feeResult = tradingFeeCalculator.calculate(
            side = order.side,
            price = price,
            quantity = order.quantity,
            feeRate = order.feeRate,
            taxRate = order.taxRate
        )

        return when (order.side) {
            OrderSide.BUY -> executeBuy(
                order = order,
                account = account,
                price = price,
                feeResult = feeResult,
                now = now
            )

            OrderSide.SELL -> executeSell(
                order = order,
                account = account,
                price = price,
                feeResult = feeResult,
                now = now
            )
        }
    }

    private fun executeBuy(
        order: Order,
        account: Account,
        price: Long,
        feeResult: TradingFeeResult,
        now: LocalDateTime
    ): Boolean {

        val newReservedBalance = releaseReservedBalance(
            account = account,
            amount = order.reservedAmount,
            orderId = order.id
        )

        val availableBalance = Math.subtractExact(
            account.balance,
            newReservedBalance
        )

        val actualAmount = feeResult.settlementAmount

        if (availableBalance < actualAmount) {
            rejectOrder(
                order = order,
                account = account,
                now = now,
                reservedBalance = newReservedBalance
            )
            return false
        }

        check(
            accountRepository.updateBalances(
                userId = account.userId,
                balance = Math.subtractExact(
                    account.balance,
                    actualAmount
                ),
                reservedBalance = newReservedBalance,
                updatedAt = now
            )
        ) {
            "매수 체결 계좌 갱신에 실패했습니다. orderId=${order.id}"
        }

        holdingProcessor.buy(
            userId = order.userId,
            stockId = order.stockId,
            quantity = order.quantity,
            price = price,
            now = now
        )

        completeOrder(
            order = order,
            price = price,
            feeResult = feeResult,
            now = now
        )

        return true
    }

    private fun executeSell(
        order: Order,
        account: Account,
        price: Long,
        feeResult: TradingFeeResult,
        now: LocalDateTime
    ): Boolean {

        val holding = holdingRepository.findByUserAndStockForUpdate(
            order.userId,
            order.stockId
        )

        if (holding == null || holding.quantity < order.quantity) {
            rejectOrder(
                order = order,
                account = account,
                now = now
            )
            return false
        }

        holdingProcessor.sell(
            holding = holding,
            quantity = order.quantity,
            now = now
        )

        check(
            accountRepository.updateBalances(
                userId = account.userId,
                balance = Math.addExact(
                    account.balance,
                    feeResult.settlementAmount
                ),
                reservedBalance = account.reservedBalance,
                updatedAt = now
            )
        ) {
            "매도 체결 계좌 갱신에 실패했습니다. orderId=${order.id}"
        }

        completeOrder(
            order = order,
            price = price,
            feeResult = feeResult,
            now = now
        )

        return true
    }

    private fun completeOrder(
        order: Order,
        price: Long,
        feeResult: TradingFeeResult,
        now: LocalDateTime
    ) {

        check(
            orderRepository.updateStatus(
                id = order.id,
                status = OrderStatus.FILLED,
                executedPrice = price,
                executedAt = now,
                cancelledAt = null,
                feeAmount = feeResult.feeAmount,
                taxAmount = feeResult.taxAmount
            )
        ) {
            "주문 체결 상태 변경에 실패했습니다. orderId=${order.id}"
        }

        transactionRepository.save(
            Transaction(
                id = 0L,
                orderId = order.id,
                userId = order.userId,
                stockId = order.stockId,
                side = order.side,
                quantity = order.quantity,
                price = price,
                totalAmount = feeResult.totalAmount,
                feeAmount = feeResult.feeAmount,
                taxAmount = feeResult.taxAmount,
                createdAt = now
            )
        )

        saveTaxCollection(
            order = order,
            taxResult = feeResult,
            now = now
        )
    }

    private fun saveTaxCollection(
        order: Order,
        taxResult: TradingFeeResult,
        now: LocalDateTime
    ) {
        if (order.side != OrderSide.SELL) return
        if (taxResult.taxAmount <= 0L) return

        taxRepository.save(
            TaxCollection(
                id = 0L,
                orderId = order.id,
                userId = order.userId,
                stockId = order.stockId,
                taxRate = taxResult.taxRate,
                taxAmount = taxResult.taxAmount,
                createdAt = now
            )
        )
    }

    private fun rejectOrder(
        order: Order,
        account: Account,
        now: LocalDateTime,
        reservedBalance: Long? = null
    ) {

        if (order.side == OrderSide.BUY) {

            val newReserved = reservedBalance ?: releaseReservedBalance(
                account = account,
                amount = order.reservedAmount,
                orderId = order.id
            )

            check(
                accountRepository.updateBalances(
                    userId = account.userId,
                    balance = account.balance,
                    reservedBalance = newReserved,
                    updatedAt = now
                )
            ) {
                "주문 예약금 반환에 실패했습니다. orderId=${order.id}"
            }
        }

        check(
            orderRepository.updateStatus(
                id = order.id,
                status = OrderStatus.REJECTED,
                executedPrice = null,
                executedAt = now,
                cancelledAt = null,
                feeAmount = 0L,
                taxAmount = 0L
            )
        ) {
            "주문 거절 처리에 실패했습니다. orderId=${order.id}"
        }
    }

    private fun releaseReservedBalance(
        account: Account,
        amount: Long,
        orderId: Long
    ): Long {

        if (amount < 0L || account.reservedBalance < amount) {
            error("예약금이 부족합니다. orderId=$orderId")
        }

        return Math.subtractExact(
            account.reservedBalance,
            amount
        )
    }
}
