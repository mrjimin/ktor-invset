package io.github.mrjimin.invest.domain.stock.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class StockPrice(
    val id: Long,
    val stockId: Long,
    val date: LocalDate,
    val open: Long,
    val high: Long,
    val low: Long,
    val close: Long,
    val createdAt: LocalDateTime
)
