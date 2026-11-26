package io.github.mrjimin.invest.domain.stock.model

import kotlinx.datetime.LocalDateTime

data class Stock(
    val id: Long,
    val code: String,
    val name: String,
    val description: String?,
    val currentPrice: Long,
    val previousClose: Long,
    val listed: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    val changeAmount: Long
        get() = currentPrice - previousClose

    val changeRate: Double
        get() = if (previousClose == 0L) 0.0
        else ((currentPrice - previousClose).toDouble() / previousClose) * 100.0
}