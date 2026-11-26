package io.github.mrjimin.invest.domain.trading.service

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.common.time.TimeProvider
import io.github.mrjimin.invest.common.time.TradingCalendar
import io.github.mrjimin.invest.domain.account.repository.AccountRepository
import io.github.mrjimin.invest.domain.stock.repository.StockRepository
import io.github.mrjimin.invest.domain.trading.dto.CreateOrderRequest
import io.github.mrjimin.invest.domain.trading.dto.OrderResponse
import io.github.mrjimin.invest.domain.trading.dto.toResponse
import io.github.mrjimin.invest.domain.trading.repository.HoldingRepository
import io.github.mrjimin.invest.domain.trading.repository.OrderRepository
import io.github.mrjimin.invest.domain.trading.repository.TransactionRepository

class OrderServiceImpl internal constructor(
    private val orderRepository: OrderRepository,
    private val placementProcessor: OrderPlacementProcessor,
    private val pendingOrderProcessor: PendingOrderProcessor,
) : OrderService {

    override fun placeOrder(
        userId: Long,
        request: CreateOrderRequest
    ): OrderResponse = withTransaction {
        placementProcessor.placeOrder(userId, request).toResponse()
    }

    override fun getMyOrders(userId: Long): List<OrderResponse> = withTransaction {
        orderRepository.findAllByUserId(userId)
            .map { it.toResponse() }
    }

    override fun executePendingOrders(): Int =
        pendingOrderProcessor.executePendingOrders()
}