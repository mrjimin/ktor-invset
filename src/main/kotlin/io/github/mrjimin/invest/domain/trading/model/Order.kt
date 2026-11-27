package io.github.mrjimin.invest.domain.trading.model

import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

data class Order(
    val id: Long,
    val userId: Long,
    val stockId: Long,
    val side: OrderSide,
    val quantity: Int,
    val requestedPrice: Long,
    val executedPrice: Long?,
    val status: OrderStatus,

    val reservedAmount: Long,
    val feeRate: BigDecimal,
    val taxRate: BigDecimal,
    val feeAmount: Long,
    val taxAmount: Long,

    val createdAt: LocalDateTime,
    val executedAt: LocalDateTime?,
    val cancelledAt: LocalDateTime?
)