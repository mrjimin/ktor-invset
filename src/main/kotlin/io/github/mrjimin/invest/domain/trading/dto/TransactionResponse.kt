package io.github.mrjimin.invest.domain.trading.dto

import io.github.mrjimin.invest.domain.trading.model.OrderSide
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
    val feeAmount: Long,
    val taxAmount: Long,
    val settlementAmount: Long,
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
    feeAmount = feeAmount,
    taxAmount = taxAmount,
    settlementAmount = when (side) {
        OrderSide.BUY ->
            Math.addExact(
                Math.addExact(totalAmount, feeAmount),
                taxAmount
            )

        OrderSide.SELL ->
            Math.subtractExact(
                Math.subtractExact(totalAmount, feeAmount),
                taxAmount
            )
    },
    createdAt = createdAt.toString()
)
