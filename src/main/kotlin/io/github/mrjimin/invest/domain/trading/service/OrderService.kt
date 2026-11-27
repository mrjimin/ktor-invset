package io.github.mrjimin.invest.domain.trading.service

import io.github.mrjimin.invest.domain.trading.dto.CreateOrderRequest
import io.github.mrjimin.invest.domain.trading.dto.OrderResponse

interface OrderService {
    fun placeOrder(
        userId: Long,
        request: CreateOrderRequest
    ): OrderResponse

    fun getMyOrders(userId: Long): List<OrderResponse>

    fun executePendingOrders(): Int

    fun cancelOrder(
        userId: Long,
        orderId: Long
    ): OrderResponse
}