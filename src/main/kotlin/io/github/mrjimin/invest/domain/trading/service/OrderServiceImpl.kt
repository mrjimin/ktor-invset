package io.github.mrjimin.invest.domain.trading.service

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.domain.trading.dto.CreateOrderRequest
import io.github.mrjimin.invest.domain.trading.dto.OrderResponse
import io.github.mrjimin.invest.domain.trading.dto.toResponse
import io.github.mrjimin.invest.domain.trading.repository.OrderRepository

class OrderServiceImpl internal constructor(
    private val orderRepository: OrderRepository,
    private val placementProcessor: OrderPlacementProcessor,
    private val pendingOrderProcessor: PendingOrderProcessor,
) : OrderService {

    override fun placeOrder(
        userId: Long,
        request: CreateOrderRequest
    ): OrderResponse = withTransaction {
        placementProcessor.placeOrder(userId, request)
            .toResponse()
    }

    override fun getMyOrders(userId: Long): List<OrderResponse> =
        orderRepository.findAllByUserId(userId)
            .map { it.toResponse() }

    override fun executePendingOrders(): Int =
        pendingOrderProcessor.executePendingOrders()
}
