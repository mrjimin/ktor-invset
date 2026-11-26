package io.github.mrjimin.invest.domain.trading.model

import kotlinx.datetime.LocalDateTime

data class Holding(
    val id: Long,
    val userId: Long,
    val stockId: Long,
    val quantity: Int,
    val averagePrice: Long,
    val updatedAt: LocalDateTime,
)