package io.github.mrjimin.invest.domain.trading.repository.table

import io.github.mrjimin.invest.domain.stock.repository.table.Stocks
import io.github.mrjimin.invest.domain.user.repository.table.Users
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.datetime

object TaxCollections : LongIdTable("tax_collections") {

    val orderId = reference(
        "order_id",
        Orders.id
    ).uniqueIndex()

    val userId = reference(
        "user_id",
        Users.id,
        onDelete = ReferenceOption.CASCADE
    )

    val stockId = reference(
        "stock_id",
        Stocks.id,
        onDelete = ReferenceOption.CASCADE
    )

    val taxRate = decimal(
        "tax_rate",
        precision = 10,
        scale = 6
    )

    val taxAmount = long("tax_amount")

    val createdAt = datetime("created_at")
}