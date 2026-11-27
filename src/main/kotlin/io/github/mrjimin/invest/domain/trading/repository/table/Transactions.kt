package io.github.mrjimin.invest.domain.trading.repository.table

import io.github.mrjimin.invest.domain.stock.repository.table.Stocks
import io.github.mrjimin.invest.domain.trading.model.OrderSide
import io.github.mrjimin.invest.domain.user.repository.table.Users
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.datetime

object Transactions : LongIdTable("transactions") {
    val orderId = reference("order_id", Orders.id).uniqueIndex()
    val userId = reference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
    val stockId = reference("stock_id", Stocks.id, onDelete = ReferenceOption.CASCADE)
    val side = enumerationByName<OrderSide>("side", 10)
    val quantity = integer("quantity")
    val price = long("price")
    val totalAmount = long("total_amount")
    val feeAmount = long("fee_amount").default(0L)
    val taxAmount = long("tax_amount").default(0L)
    val createdAt = datetime("created_at")
}