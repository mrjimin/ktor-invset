package io.github.mrjimin.invest.domain.stock.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class CreateStockPriceRequest(
    val date: LocalDate,
    val open: Long,
    val high: Long,
    val low: Long,
    val close: Long
)