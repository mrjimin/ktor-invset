package io.github.mrjimin.invest.domain.trading.service

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
import io.github.mrjimin.invest.domain.trading.model.Transaction
import io.github.mrjimin.invest.domain.trading.repository.HoldingRepository
import io.github.mrjimin.invest.domain.trading.repository.OrderRepository
import io.github.mrjimin.invest.domain.trading.repository.TransactionRepository
import kotlinx.datetime.LocalDateTime

internal class OrderPlacementProcessor(
    private val accountRepository: AccountRepository,
    private val stockRepository: StockRepository,
    private val holdingRepository: HoldingRepository,
    private val orderRepository: OrderRepository,
    private val transactionRepository: TransactionRepository,
    private val timeProvider: TimeProvider,
    private val tradingCalendar: TradingCalendar,
    private val holdingProcessor: HoldingProcessor,
) {

    fun placeOrder(
        userId: Long,
        request: CreateOrderRequest
    ): Order {
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
        val totalAmount = calculateTotal(price, request.quantity)

        return when (request.side) {
            OrderSide.BUY -> placeBuyOrder(
                account,
                stock.id,
                request.quantity,
                price,
                totalAmount,
                isMarketOpen,
                now
            )

            OrderSide.SELL -> placeSellOrder(
                account,
                stock.id,
                request.quantity,
                price,
                totalAmount,
                isMarketOpen,
                now
            )
        }
    }

    private fun placeBuyOrder(
        account: Account,
        stockId: Long,
        quantity: Int,
        price: Long,
        totalAmount: Long,
        isMarketOpen: Boolean,
        now: LocalDateTime
    ): Order {

        if (account.availableBalance < totalAmount) {
            throw ApiException(ApiErrorCode.INSUFFICIENT_BALANCE)
        }

        if (isMarketOpen) {

            check(
                accountRepository.updateBalances(
                    account.userId,
                    Math.subtractExact(account.balance, totalAmount),
                    account.reservedBalance,
                    now
                )
            ) {
                "계좌 잔액 갱신에 실패했습니다."
            }

            holdingProcessor.buy(
                account.userId,
                stockId,
                quantity,
                price,
                now
            )

            return createFilledOrder(
                account.userId,
                stockId,
                OrderSide.BUY,
                quantity,
                price,
                totalAmount,
                now
            )
        }

        check(
            accountRepository.updateBalances(
                account.userId,
                account.balance,
                Math.addExact(account.reservedBalance, totalAmount),
                now
            )
        ) {
            "주문 예약금 갱신에 실패했습니다."
        }

        return createPendingOrder(
            account.userId,
            stockId,
            OrderSide.BUY,
            quantity,
            price,
            now
        )
    }

    private fun placeSellOrder(
        account: Account,
        stockId: Long,
        quantity: Int,
        price: Long,
        totalAmount: Long,
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

//        if (holding.quantity - pendingQuantity < quantity) {
//            throw ApiException(ApiErrorCode.INSUFFICIENT_HOLDINGS)
//        }

        if (isMarketOpen) {
            holdingProcessor.sell(
                holding,
                quantity,
                now
            )

            check(
                accountRepository.updateBalances(
                    account.userId,
                    Math.addExact(account.balance, totalAmount),
                    account.reservedBalance,
                    now
                )
            ) {
                "계좌 잔액 갱신에 실패했습니다."
            }

            return createFilledOrder(
                account.userId,
                stockId,
                OrderSide.SELL,
                quantity,
                price,
                totalAmount,
                now
            )
        }

        return createPendingOrder(
            account.userId,
            stockId,
            OrderSide.SELL,
            quantity,
            price,
            now
        )
    }

    private fun createFilledOrder(
        userId: Long,
        stockId: Long,
        side: OrderSide,
        quantity: Int,
        price: Long,
        totalAmount: Long,
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
                createdAt = now,
                executedAt = now
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
                totalAmount = totalAmount,
                createdAt = now
            )
        )

        return order
    }

    private fun createPendingOrder(
        userId: Long,
        stockId: Long,
        side: OrderSide,
        quantity: Int,
        price: Long,
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
            createdAt = now,
            executedAt = null
        )
    )

    private fun calculateTotal(
        price: Long,
        quantity: Int
    ): Long {
        return Math.multiplyExact(price, quantity.toLong())
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
