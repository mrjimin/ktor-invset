package io.github.mrjimin.invest.domain.trading.dto

import io.github.mrjimin.invest.domain.trading.model.OrderSide
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val stockId: Long,
    val side: OrderSide,
    val quantity: Int,
)