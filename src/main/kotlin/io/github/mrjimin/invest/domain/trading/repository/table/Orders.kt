package io.github.mrjimin.invest.domain.trading.repository.table

import io.github.mrjimin.invest.domain.stock.repository.table.Stocks
import io.github.mrjimin.invest.domain.trading.model.OrderSide
import io.github.mrjimin.invest.domain.trading.model.OrderStatus
import io.github.mrjimin.invest.domain.user.repository.table.Users
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.datetime

object Orders : LongIdTable("orders") {
    val userId = reference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
    val stockId = reference("stock_id", Stocks.id, onDelete = ReferenceOption.CASCADE)
    val side = enumerationByName<OrderSide>("side", 10)
    val quantity = integer("quantity")
    val requestedPrice = long("requested_price")
    val executedPrice = long("executed_price").nullable()
    val status = enumerationByName<OrderStatus>("status", 20)
    val createdAt = datetime("created_at")
    val executedAt = datetime("executed_at").nullable()
}