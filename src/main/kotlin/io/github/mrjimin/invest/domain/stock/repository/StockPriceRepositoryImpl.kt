package io.github.mrjimin.invest.domain.stock.repository

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.domain.stock.model.StockPrice
import io.github.mrjimin.invest.domain.stock.repository.table.StockPrices
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll

class StockPriceRepositoryImpl : StockPriceRepository {

    override fun findByStockIdAndDate(
        stockId: Long,
        date: LocalDate
    ): StockPrice? = withTransaction {
        StockPrices.selectAll()
            .where {
                (StockPrices.stockId eq stockId) and
                        (StockPrices.date eq date)
            }
            .singleOrNull()
            ?.toStockPrice()
    }

    override fun findAllByStockIdAndDateBetween(
        stockId: Long,
        from: LocalDate,
        to: LocalDate
    ): List<StockPrice> = withTransaction {
        StockPrices.selectAll()
            .where {
                (StockPrices.stockId eq stockId) and
                        (StockPrices.date greaterEq from) and
                        (StockPrices.date lessEq to)
            }
            .orderBy(StockPrices.date to SortOrder.ASC)
            .map { it.toStockPrice() }
    }

    override fun findLatestByStockId(
        stockId: Long
    ): StockPrice? = withTransaction {
        StockPrices.selectAll()
            .where {
                StockPrices.stockId eq stockId
            }
            .orderBy(StockPrices.date to SortOrder.DESC)
            .limit(1)
            .singleOrNull()
            ?.toStockPrice()
    }

    override fun save(
        stockPrice: StockPrice
    ): StockPrice = withTransaction {
        val id = StockPrices.insertAndGetId {
            it[StockPrices.stockId] = stockPrice.stockId
            it[date] = stockPrice.date
            it[open] = stockPrice.open
            it[high] = stockPrice.high
            it[low] = stockPrice.low
            it[close] = stockPrice.close
            it[createdAt] = stockPrice.createdAt
        }.value

        StockPrices.selectAll()
            .where { StockPrices.id eq id }
            .singleOrNull()
            ?.toStockPrice()
            ?: error("생성된 시세 정보를 조회할 수 없습니다.")
    }

    private fun ResultRow.toStockPrice() = StockPrice(
        id = this[StockPrices.id].value,
        stockId = this[StockPrices.stockId].value,
        date = this[StockPrices.date],
        open = this[StockPrices.open],
        high = this[StockPrices.high],
        low = this[StockPrices.low],
        close = this[StockPrices.close],
        createdAt = this[StockPrices.createdAt]
    )
}