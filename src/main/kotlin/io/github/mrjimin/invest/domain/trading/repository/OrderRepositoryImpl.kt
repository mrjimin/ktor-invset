package io.github.mrjimin.invest.domain.trading.repository

import io.github.mrjimin.invest.domain.trading.model.Order
import io.github.mrjimin.invest.domain.trading.model.OrderSide
import io.github.mrjimin.invest.domain.trading.model.OrderStatus
import io.github.mrjimin.invest.domain.trading.repository.table.Orders
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class OrderRepositoryImpl : OrderRepository {

    override fun save(order: Order): Order {
        val id = Orders.insertAndGetId {
            it[userId] = order.userId
            it[stockId] = order.stockId
            it[side] = order.side
            it[quantity] = order.quantity
            it[requestedPrice] = order.requestedPrice
            it[executedPrice] = order.executedPrice
            it[status] = order.status
            it[createdAt] = order.createdAt
            it[executedAt] = order.executedAt
        }.value

        return order.copy(id = id)
    }

    override fun findByIdForUpdate(id: Long): Order? =
        Orders.selectAll()
            .where { Orders.id eq id }
            .forUpdate()
            .singleOrNull()
            ?.toOrder()

    override fun findAllByUserId(userId: Long): List<Order> =
        Orders.selectAll()
            .where { Orders.userId eq userId }
            .map { it.toOrder() }

    override fun findPendingOrderIds(): List<Long> =
        Orders.selectAll()
            .where { Orders.status eq OrderStatus.PENDING }
            .map { it[Orders.id].value }

    override fun sumPendingSellQuantity(
        userId: Long,
        stockId: Long
    ): Int =
        Orders.selectAll()
            .where {
                (Orders.userId eq userId) and
                        (Orders.stockId eq stockId) and
                        (Orders.side eq OrderSide.SELL) and
                        (Orders.status eq OrderStatus.PENDING)
            }
            .sumOf { it[Orders.quantity] }

    override fun updateStatus(
        id: Long,
        status: OrderStatus,
        executedPrice: Long?,
        executedAt: LocalDateTime
    ): Boolean =
        Orders.update({
            (Orders.id eq id) and
                    (Orders.status eq OrderStatus.PENDING)
        }) {
            it[Orders.status] = status
            it[Orders.executedPrice] = executedPrice
            it[Orders.executedAt] = executedAt
        } > 0

    private fun ResultRow.toOrder() = Order(
        id = this[Orders.id].value,
        userId = this[Orders.userId].value,
        stockId = this[Orders.stockId].value,
        side = this[Orders.side],
        quantity = this[Orders.quantity],
        requestedPrice = this[Orders.requestedPrice],
        executedPrice = this[Orders.executedPrice],
        status = this[Orders.status],
        createdAt = this[Orders.createdAt],
        executedAt = this[Orders.executedAt],
    )
}
