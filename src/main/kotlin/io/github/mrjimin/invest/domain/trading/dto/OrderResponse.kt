package io.github.mrjimin.invest.domain.trading.dto

import io.github.mrjimin.invest.domain.trading.model.Order
import io.github.mrjimin.invest.domain.trading.model.OrderSide
import io.github.mrjimin.invest.domain.trading.model.OrderStatus
import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    val id: Long,
    val stockId: Long,
    val side: OrderSide,
    val quantity: Int,
    val requestedPrice: Long,
    val executedPrice: Long?,
    val status: OrderStatus,
    val createdAt: String,
    val executedAt: String?,
)

fun Order.toResponse() = OrderResponse(
    id = id,
    stockId = stockId,
    side = side,
    quantity = quantity,
    requestedPrice = requestedPrice,
    executedPrice = executedPrice,
    status = status,
    createdAt = createdAt.toString(),
    executedAt = executedAt?.toString(),
)