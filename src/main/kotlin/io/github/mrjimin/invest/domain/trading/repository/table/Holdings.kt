package io.github.mrjimin.invest.domain.trading.repository.table

import io.github.mrjimin.invest.domain.stock.repository.table.Stocks
import io.github.mrjimin.invest.domain.user.repository.table.Users
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.datetime

object Holdings : LongIdTable("holdings") {
    val userId = reference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
    val stockId = reference("stock_id", Stocks.id, onDelete = ReferenceOption.CASCADE)
    val quantity = integer("quantity")
    val averagePrice = long("average_price")
    val updatedAt = datetime("updated_at")

    init {
        uniqueIndex(userId, stockId)
    }
}