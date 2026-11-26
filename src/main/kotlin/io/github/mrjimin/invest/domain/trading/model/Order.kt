package io.github.mrjimin.invest.domain.trading.model

import kotlinx.datetime.LocalDateTime

data class Order(
    val id: Long,
    val userId: Long,
    val stockId: Long,
    val side: OrderSide,
    val quantity: Int,
    val requestedPrice: Long,
    val executedPrice: Long?,
    val status: OrderStatus,
    val createdAt: LocalDateTime,
    val executedAt: LocalDateTime?,
)