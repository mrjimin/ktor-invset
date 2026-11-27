package io.github.mrjimin.invest.domain.trading.model

import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

data class TaxCollection(
    val id: Long,
    val orderId: Long,
    val userId: Long,
    val stockId: Long,
    val taxRate: BigDecimal,
    val taxAmount: Long,
    val createdAt: LocalDateTime
)