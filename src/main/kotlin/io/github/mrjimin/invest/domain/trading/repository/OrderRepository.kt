package io.github.mrjimin.invest.domain.trading.repository

import io.github.mrjimin.invest.domain.trading.model.Order
import io.github.mrjimin.invest.domain.trading.model.OrderStatus
import kotlinx.datetime.LocalDateTime

interface OrderRepository {
    fun save(order: Order): Order

    fun findByIdForUpdate(id: Long): Order?
    fun findAllByUserId(userId: Long): List<Order>
    fun findPendingOrderIds(): List<Long>

    fun sumPendingSellQuantity(userId: Long, stockId: Long): Int

    fun updateStatus(
        id: Long,
        status: OrderStatus,
        executedPrice: Long?,
        executedAt: LocalDateTime
    ): Boolean
}