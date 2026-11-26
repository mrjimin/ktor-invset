package io.github.mrjimin.invest.domain.stock.repository.table

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.datetime

object Stocks : LongIdTable("stocks") {
    val code = varchar("code", 20).uniqueIndex()
    val name = varchar("name", 100)
    val description = text("description").nullable()
    val currentPrice = long("current_price")
    val previousClose = long("previous_close")
    val listed = bool("listed").default(true)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}