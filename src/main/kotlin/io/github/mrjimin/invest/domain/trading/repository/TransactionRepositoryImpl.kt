package io.github.mrjimin.invest.domain.trading.repository

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.domain.trading.model.Transaction
import io.github.mrjimin.invest.domain.trading.repository.table.Transactions
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll

class TransactionRepositoryImpl : TransactionRepository {

    override fun save(
        transaction: Transaction
    ): Transaction = withTransaction {

        val id = Transactions.insertAndGetId {
            it[orderId] = transaction.orderId
            it[userId] = transaction.userId
            it[stockId] = transaction.stockId
            it[side] = transaction.side
            it[quantity] = transaction.quantity
            it[price] = transaction.price
            it[totalAmount] = transaction.totalAmount
            it[feeAmount] = transaction.feeAmount
            it[taxAmount] = transaction.taxAmount
            it[createdAt] = transaction.createdAt
        }.value

        transaction.copy(id = id)
    }

    override fun findAllByUserId(
        userId: Long
    ): List<Transaction> = withTransaction {

        Transactions.selectAll()
            .where { Transactions.userId eq userId }
            .orderBy(
                Transactions.createdAt to SortOrder.DESC
            )
            .map { it.toTransaction() }
    }

    private fun ResultRow.toTransaction() = Transaction(
        id = this[Transactions.id].value,
        orderId = this[Transactions.orderId].value,
        userId = this[Transactions.userId].value,
        stockId = this[Transactions.stockId].value,
        side = this[Transactions.side],
        quantity = this[Transactions.quantity],
        price = this[Transactions.price],
        totalAmount = this[Transactions.totalAmount],
        feeAmount = this[Transactions.feeAmount],
        taxAmount = this[Transactions.taxAmount],
        createdAt = this[Transactions.createdAt]
    )
}
