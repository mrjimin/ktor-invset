package io.github.mrjimin.invest.domain.trading.dto

import io.github.mrjimin.invest.domain.trading.model.Transaction
import kotlinx.serialization.Serializable

@Serializable
data class TransactionResponse(
    val id: Long,
    val orderId: Long,
    val stockId: Long,
    val side: String,
    val quantity: Int,
    val price: Long,
    val totalAmount: Long,
    val createdAt: String
)

fun Transaction.toResponse() = TransactionResponse(
    id = id,
    orderId = orderId,
    stockId = stockId,
    side = side.name,
    quantity = quantity,
    price = price,
    totalAmount = totalAmount,
    createdAt = createdAt.toString()
)
