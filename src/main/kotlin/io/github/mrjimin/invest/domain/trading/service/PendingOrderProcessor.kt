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
import io.github.mrjimin.invest.domain.trading.model.Transaction
import io.github.mrjimin.invest.domain.trading.repository.HoldingRepository
import io.github.mrjimin.invest.domain.trading.repository.OrderRepository
import io.github.mrjimin.invest.domain.trading.repository.TransactionRepository
import kotlinx.datetime.LocalDateTime

internal class PendingOrderProcessor(
    private val accountRepository: AccountRepository,
    private val stockRepository: StockRepository,
    private val orderRepository: OrderRepository,
    private val transactionRepository: TransactionRepository,
    private val timeProvider: TimeProvider,
    private val tradingCalendar: TradingCalendar,
    private val holdingProcessor: HoldingProcessor,
    private val holdingRepository: HoldingRepository,
) {

    fun executePendingOrders(): Int {
        if (!tradingCalendar.isMarketOpen()) return 0

        val now = timeProvider.nowUtc()

        val pendingOrderIds = withTransaction {
            orderRepository.findPendingOrderIds()
        }

        return pendingOrderIds.count { orderId ->
            withTransaction {
                executeOrder(orderId, now)
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
            ?: return false

        val requestedTotal = calculateTotal(
            order.requestedPrice, order.quantity
        )

        val stock = stockRepository.findById(order.stockId)

        if (stock == null || !stock.listed) {
            rejectOrder(order, account, requestedTotal, now)
            return false
        }

        val price = stock.currentPrice
        val actualTotal = calculateTotal(price, order.quantity)

        return when (order.side) {
            OrderSide.BUY -> executeBuy(
                order, account, price, actualTotal, requestedTotal, now
            )

            OrderSide.SELL -> executeSell(
                order, account, price, actualTotal, requestedTotal, now
            )
        }
    }

    private fun executeBuy(
        order: Order,
        account: Account,
        price: Long,
        actualTotal: Long,
        requestedTotal: Long,
        now: LocalDateTime
    ): Boolean {
        val newReserved = (
                account.reservedBalance - requestedTotal
                ).coerceAtLeast(0L)

        if (account.balance - newReserved < actualTotal) {
            rejectOrder(order, account, requestedTotal, now)
            return false
        }

        check(
            accountRepository.updateBalances(
                account.userId,
                Math.subtractExact(account.balance, actualTotal),
                newReserved,
                now
            )
        ) {
            "매수 체결 계좌 갱신에 실패했습니다."
        }

        holdingProcessor.buy(
            order.userId, order.stockId, order.quantity, price, now
        )

        completeOrder(order, price, actualTotal, now)
        return true
    }

    private fun executeSell(
        order: Order,
        account: Account,
        price: Long,
        actualTotal: Long,
        requestedTotal: Long,
        now: LocalDateTime
    ): Boolean {
        val holding = holdingRepository.findByUserAndStockForUpdate(
            order.userId, order.stockId
        )

        if (holding == null || holding.quantity < order.quantity) {
            rejectOrder(order, account, requestedTotal, now)
            return false
        }

        holdingProcessor.sell(holding, order.quantity, now)

        check(
            accountRepository.updateBalances(
                account.userId,
                Math.addExact(account.balance, actualTotal),
                account.reservedBalance,
                now
            )
        ) {
            "매도 체결 계좌 갱신에 실패했습니다."
        }

        completeOrder(order, price, actualTotal, now)
        return true
    }

    private fun completeOrder(
        order: Order,
        price: Long,
        totalAmount: Long,
        now: LocalDateTime
    ) {
        val updated = orderRepository.updateStatus(
            order.id, OrderStatus.FILLED, price, now
        )

        check(updated) {
            "주문 상태 변경에 실패했습니다. orderId=${order.id}"
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
                totalAmount = totalAmount,
                createdAt = now
            )
        )
    }

    private fun rejectOrder(
        order: Order,
        account: Account,
        requestedTotal: Long,
        now: LocalDateTime
    ) {
        if (order.side == OrderSide.BUY) {
            val newReserved = Math.subtractExact(
                account.reservedBalance,
                requestedTotal
            )

            check(
                accountRepository.updateBalances(
                    account.userId,
                    account.balance,
                    newReserved,
                    now
                )
            ) {
                "주문 예약금 반환에 실패했습니다."
            }
        }

        val updated = orderRepository.updateStatus(
            order.id, OrderStatus.REJECTED, null, now
        )

        check(updated) {
            "주문 거절 처리에 실패했습니다. orderId=${order.id}"
        }
    }

    private fun calculateTotal(
        price: Long,
        quantity: Int
    ): Long = Math.multiplyExact(price, quantity.toLong())
}
