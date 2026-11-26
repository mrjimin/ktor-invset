package io.github.mrjimin.invest.domain.trading.repository

import io.github.mrjimin.invest.domain.trading.model.Holding
import kotlinx.datetime.LocalDateTime

interface HoldingRepository {
    fun findByUserAndStockForUpdate(userId: Long, stockId: Long): Holding?
    fun findAllByUserId(userId: Long): List<Holding>

    fun save(holding: Holding): Holding

    fun update(
        id: Long,
        quantity: Int,
        averagePrice: Long,
        updatedAt: LocalDateTime
    ): Boolean

    fun deleteByUserAndStock(userId: Long, stockId: Long): Boolean
}