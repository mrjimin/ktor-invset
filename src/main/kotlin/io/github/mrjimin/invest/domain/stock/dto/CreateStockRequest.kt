package io.github.mrjimin.invest.domain.stock.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateStockRequest(
    val code: String,
    val name: String,
    val description: String? = null,
    val initialPrice: Long,
)