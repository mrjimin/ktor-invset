package io.github.mrjimin.invest.domain.trading.service

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.common.time.TimeProvider
import io.github.mrjimin.invest.common.time.TradingCalendar
import io.github.mrjimin.invest.domain.account.model.Account
import io.github.mrjimin.invest.domain.account.repository.AccountRepository
import io.github.mrjimin.invest.domain.stock.repository.StockRepository
import io.github.mrjimin.invest.domain.trading.dto.CreateOrderRequest
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

internal class OrderPlacementProcessor(
    private val accountRepository: AccountRepository,
    private val stockRepository: StockRepository,
    private val holdingRepository: HoldingRepository,
    private val orderRepository: OrderRepository,
    private val transactionRepository: TransactionRepository,
    private val taxRepository: TaxRepository,
    private val timeProvider: TimeProvider,
    private val tradingCalendar: TradingCalendar,
    private val holdingProcessor: HoldingProcessor,
    private val tradingFeeCalculator: TradingFeeCalculator
) {

    fun placeOrder(
        userId: Long,
        request: CreateOrderRequest
    ): Order = withTransaction {

        validateQuantity(request.quantity)

        val now = timeProvider.nowUtc()
        val isMarketOpen = tradingCalendar.isMarketOpen()

        val account = accountRepository.findByUserIdForUpdate(userId)
            ?: throw ApiException(ApiErrorCode.ACCOUNT_NOT_FOUND)

        val stock = stockRepository.findById(request.stockId)
            ?: throw ApiException(ApiErrorCode.STOCK_NOT_FOUND)

        if (!stock.listed) {
            throw ApiException(ApiErrorCode.STOCK_NOT_LISTED)
        }

        val price = stock.currentPrice

        val feeResult = tradingFeeCalculator.calculate(
            side = request.side,
            price = price,
            quantity = request.quantity
        )

        when (request.side) {
            OrderSide.BUY -> placeBuyOrder(
                account = account,
                stockId = stock.id,
                quantity = request.quantity,
                price = price,
                feeResult = feeResult,
                isMarketOpen = isMarketOpen,
                now = now
            )

            OrderSide.SELL -> placeSellOrder(
                account = account,
                stockId = stock.id,
                quantity = request.quantity,
                price = price,
                feeResult = feeResult,
                isMarketOpen = isMarketOpen,
                now = now
            )
        }
    }

    private fun placeBuyOrder(
        account: Account,
        stockId: Long,
        quantity: Int,
        price: Long,
        feeResult: TradingFeeResult,
        isMarketOpen: Boolean,
        now: LocalDateTime
    ): Order {

        val reservedAmount = feeResult.settlementAmount

        if (account.availableBalance < reservedAmount) {
            throw ApiException(ApiErrorCode.INSUFFICIENT_BALANCE)
        }

        if (isMarketOpen) {

            check(
                accountRepository.updateBalances(
                    userId = account.userId,
                    balance = Math.subtractExact(
                        account.balance,
                        reservedAmount
                    ),
                    reservedBalance = account.reservedBalance,
                    updatedAt = now
                )
            ) {
                "매수 주문 계좌 갱신에 실패했습니다."
            }

            holdingProcessor.buy(
                userId = account.userId,
                stockId = stockId,
                quantity = quantity,
                price = price,
                now = now
            )

            return createFilledOrder(
                userId = account.userId,
                stockId = stockId,
                side = OrderSide.BUY,
                quantity = quantity,
                price = price,
                feeResult = feeResult,
                reservedAmount = reservedAmount,
                now = now
            )
        }

        check(
            accountRepository.updateBalances(
                userId = account.userId,
                balance = account.balance,
                reservedBalance = Math.addExact(
                    account.reservedBalance,
                    reservedAmount
                ),
                updatedAt = now
            )
        ) {
            "매수 주문 예약금 갱신에 실패했습니다."
        }

        return createPendingOrder(
            userId = account.userId,
            stockId = stockId,
            side = OrderSide.BUY,
            quantity = quantity,
            price = price,
            reservedAmount = reservedAmount,
            feeResult = feeResult,
            now = now
        )
    }

    private fun placeSellOrder(
        account: Account,
        stockId: Long,
        quantity: Int,
        price: Long,
        feeResult: TradingFeeResult,
        isMarketOpen: Boolean,
        now: LocalDateTime
    ): Order {

        val holding = holdingRepository.findByUserAndStockForUpdate(
            account.userId,
            stockId
        ) ?: throw ApiException(ApiErrorCode.INSUFFICIENT_HOLDINGS)

        val pendingQuantity = orderRepository.sumPendingSellQuantity(
            account.userId,
            stockId
        )

        val availableQuantity = holding.quantity.toLong() -
                pendingQuantity.toLong()

        if (availableQuantity < quantity.toLong()) {
            throw ApiException(ApiErrorCode.INSUFFICIENT_HOLDINGS)
        }

        if (isMarketOpen) {

            holdingProcessor.sell(
                holding = holding,
                quantity = quantity,
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
                "매도 주문 계좌 갱신에 실패했습니다."
            }

            return createFilledOrder(
                userId = account.userId,
                stockId = stockId,
                side = OrderSide.SELL,
                quantity = quantity,
                price = price,
                feeResult = feeResult,
                reservedAmount = 0L,
                now = now
            )
        }

        return createPendingOrder(
            userId = account.userId,
            stockId = stockId,
            side = OrderSide.SELL,
            quantity = quantity,
            price = price,
            reservedAmount = 0L,
            feeResult = feeResult,
            now = now
        )
    }

    private fun createFilledOrder(
        userId: Long,
        stockId: Long,
        side: OrderSide,
        quantity: Int,
        price: Long,
        feeResult: TradingFeeResult,
        reservedAmount: Long,
        now: LocalDateTime
    ): Order {

        val order = orderRepository.save(
            Order(
                id = 0L,
                userId = userId,
                stockId = stockId,
                side = side,
                quantity = quantity,
                requestedPrice = price,
                executedPrice = price,
                status = OrderStatus.FILLED,
                reservedAmount = reservedAmount,
                feeRate = feeResult.feeRate,
                taxRate = feeResult.taxRate,
                feeAmount = feeResult.feeAmount,
                taxAmount = feeResult.taxAmount,
                createdAt = now,
                executedAt = now,
                cancelledAt = null
            )
        )

        transactionRepository.save(
            Transaction(
                id = 0L,
                orderId = order.id,
                userId = userId,
                stockId = stockId,
                side = side,
                quantity = quantity,
                price = price,
                totalAmount = feeResult.totalAmount,
                feeAmount = feeResult.feeAmount,
                taxAmount = feeResult.taxAmount,
                createdAt = now
            )
        )

        saveTaxCollection(
            order = order,
            now = now
        )

        return order
    }

    private fun createPendingOrder(
        userId: Long,
        stockId: Long,
        side: OrderSide,
        quantity: Int,
        price: Long,
        reservedAmount: Long,
        feeResult: TradingFeeResult,
        now: LocalDateTime
    ): Order = orderRepository.save(
        Order(
            id = 0L,
            userId = userId,
            stockId = stockId,
            side = side,
            quantity = quantity,
            requestedPrice = price,
            executedPrice = null,
            status = OrderStatus.PENDING,
            reservedAmount = reservedAmount,
            feeRate = feeResult.feeRate,
            taxRate = feeResult.taxRate,
            feeAmount = 0L,
            taxAmount = 0L,
            createdAt = now,
            executedAt = null,
            cancelledAt = null
        )
    )

    private fun saveTaxCollection(
        order: Order,
        now: LocalDateTime
    ) {
        if (order.side != OrderSide.SELL) return
        if (order.taxAmount <= 0L) return

        taxRepository.save(
            TaxCollection(
                id = 0L,
                orderId = order.id,
                userId = order.userId,
                stockId = order.stockId,
                taxRate = order.taxRate,
                taxAmount = order.taxAmount,
                createdAt = now
            )
        )
    }

    private fun validateQuantity(quantity: Int) {
        if (quantity <= 0) {
            throw ApiException(
                ApiErrorCode.INVALID_REQUEST,
                "주문 수량은 1주 이상이어야 합니다."
            )
        }
    }
}
