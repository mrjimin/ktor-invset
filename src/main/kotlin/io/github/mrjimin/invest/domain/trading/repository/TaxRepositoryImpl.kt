package io.github.mrjimin.invest.domain.trading.repository

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.domain.trading.model.TaxCollection
import io.github.mrjimin.invest.domain.trading.repository.table.TaxCollections
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll

class TaxRepositoryImpl : TaxRepository {

    override fun save(
        taxCollection: TaxCollection
    ): TaxCollection = withTransaction {

        require(taxCollection.taxAmount > 0L) {
            "세금 징수 금액은 0보다 커야 합니다."
        }

        val id = TaxCollections.insertAndGetId {
            it[orderId] = taxCollection.orderId
            it[userId] = taxCollection.userId
            it[stockId] = taxCollection.stockId
            it[taxRate] = taxCollection.taxRate
            it[taxAmount] = taxCollection.taxAmount
            it[createdAt] = taxCollection.createdAt
        }.value

        taxCollection.copy(id = id)
    }

    override fun findAll(): List<TaxCollection> =
        withTransaction {
            TaxCollections.selectAll()
                .orderBy(
                    TaxCollections.createdAt to SortOrder.DESC
                )
                .map { it.toTaxCollection() }
        }

    override fun sumTaxAmount(): Long =
        withTransaction {
            TaxCollections.selectAll()
                .sumOf {
                    it[TaxCollections.taxAmount]
                }
        }

    private fun ResultRow.toTaxCollection() = TaxCollection(
        id = this[TaxCollections.id].value,
        orderId = this[TaxCollections.orderId].value,
        userId = this[TaxCollections.userId].value,
        stockId = this[TaxCollections.stockId].value,
        taxRate = this[TaxCollections.taxRate],
        taxAmount = this[TaxCollections.taxAmount],
        createdAt = this[TaxCollections.createdAt]
    )
}