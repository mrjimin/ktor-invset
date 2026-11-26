package io.github.mrjimin.invest.domain.stock.repository.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.datetime

object StockPrices : LongIdTable("stock_prices") {
    val stockId = reference("stock_id", Stocks.id, onDelete = ReferenceOption.CASCADE)
    val date = date("date")
    val open = long("open")
    val high = long("high")
    val low = long("low")
    val close = long("close")
    val createdAt = datetime("created_at")

    init {
        uniqueIndex(stockId, date)
    }
}