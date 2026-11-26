package io.github.mrjimin.invest.domain.stock.dto

import io.github.mrjimin.invest.domain.stock.model.Stock
import kotlinx.serialization.Serializable

@Serializable
data class StockResponse(
    val id: Long,
    val code: String,
    val name: String,
    val description: String?,
    val currentPrice: Long,
    val previousClose: Long,
    val changeAmount: Long,
    val changeRate: Double,
    val listed: Boolean,
    val createdAt: String,
    val updatedAt: String,
)

fun Stock.toResponse() = StockResponse(
    id = id,
    code = code,
    name = name,
    description = description,
    currentPrice = currentPrice,
    previousClose = previousClose,
    changeAmount = changeAmount,
    changeRate = changeRate,
    listed = listed,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString(),
)