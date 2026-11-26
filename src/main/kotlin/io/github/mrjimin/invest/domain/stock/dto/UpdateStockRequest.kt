package io.github.mrjimin.invest.domain.stock.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateStockRequest(
    val name: String? = null,
    val description: String? = null,
    val listed: Boolean? = null,
)