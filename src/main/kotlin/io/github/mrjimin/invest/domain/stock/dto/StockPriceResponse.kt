package io.github.mrjimin.invest.domain.stock.dto

import io.github.mrjimin.invest.domain.stock.model.StockPrice
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class StockPriceResponse(
    val id: Long,
    val stockId: Long,
    val date: LocalDate,
    val open: Long,
    val high: Long,
    val low: Long,
    val close: Long,
    val createdAt: LocalDateTime
)

fun StockPrice.toResponse() = StockPriceResponse(
    id = id,
    stockId = stockId,
    date = date,
    open = open,
    high = high,
    low = low,
    close = close,
    createdAt = createdAt
)