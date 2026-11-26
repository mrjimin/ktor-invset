package io.github.mrjimin.invest.domain.trading.dto

import io.github.mrjimin.invest.domain.trading.model.Holding
import kotlinx.serialization.Serializable

@Serializable
data class HoldingResponse(
    val id: Long,
    val stockId: Long,
    val quantity: Int,
    val averagePrice: Long,
    val updatedAt: String
)

fun Holding.toResponse() = HoldingResponse(
    id = id,
    stockId = stockId,
    quantity = quantity,
    averagePrice = averagePrice,
    updatedAt = updatedAt.toString()
)
