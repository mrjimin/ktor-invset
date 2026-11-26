package io.github.mrjimin.invest.domain.trading.model

import kotlinx.datetime.LocalDateTime

data class Transaction(
    val id: Long,
    val orderId: Long,
    val userId: Long,
    val stockId: Long,
    val side: OrderSide,
    val quantity: Int,
    val price: Long,
    val totalAmount: Long,
    val createdAt: LocalDateTime,
)