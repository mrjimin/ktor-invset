package io.github.mrjimin.invest.domain.trading.repository

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.domain.trading.model.Holding
import io.github.mrjimin.invest.domain.trading.repository.table.Holdings
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class HoldingRepositoryImpl : HoldingRepository {

    override fun findByUserAndStockForUpdate(
        userId: Long,
        stockId: Long
    ): Holding? = withTransaction {
        Holdings.selectAll()
            .where {
                (Holdings.userId eq userId) and
                        (Holdings.stockId eq stockId)
            }
            .forUpdate()
            .singleOrNull()
            ?.toHolding()
    }

    override fun findAllByUserId(userId: Long): List<Holding> =
        withTransaction {
            Holdings.selectAll()
                .where { Holdings.userId eq userId }
                .map { it.toHolding() }
        }

    override fun save(holding: Holding): Holding =
        withTransaction {
            val id = Holdings.insertAndGetId {
                it[userId] = holding.userId
                it[stockId] = holding.stockId
                it[quantity] = holding.quantity
                it[averagePrice] = holding.averagePrice
                it[updatedAt] = holding.updatedAt
            }.value

            holding.copy(id = id)
        }

    override fun update(
        id: Long,
        quantity: Int,
        averagePrice: Long,
        updatedAt: LocalDateTime
    ): Boolean = withTransaction {
        Holdings.update({ Holdings.id eq id }) {
            it[Holdings.quantity] = quantity
            it[Holdings.averagePrice] = averagePrice
            it[Holdings.updatedAt] = updatedAt
        } > 0
    }

    override fun deleteByUserAndStock(
        userId: Long,
        stockId: Long
    ): Boolean = withTransaction {
        Holdings.deleteWhere {
            (Holdings.userId eq userId) and
                    (Holdings.stockId eq stockId)
        } > 0
    }

    private fun ResultRow.toHolding() = Holding(
        id = this[Holdings.id].value,
        userId = this[Holdings.userId].value,
        stockId = this[Holdings.stockId].value,
        quantity = this[Holdings.quantity],
        averagePrice = this[Holdings.averagePrice],
        updatedAt = this[Holdings.updatedAt],
    )
}
