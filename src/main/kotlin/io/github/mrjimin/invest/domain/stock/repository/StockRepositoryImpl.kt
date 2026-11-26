package io.github.mrjimin.invest.domain.stock.repository

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.domain.stock.model.Stock
import io.github.mrjimin.invest.domain.stock.repository.table.Stocks
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class StockRepositoryImpl : StockRepository {

    override fun findAll(onlyListed: Boolean): List<Stock> = withTransaction {
        val query = Stocks.selectAll()

        if (onlyListed) {
            query.where { Stocks.listed eq true }
        }

        query.map { it.toStock() }
    }

    override fun findById(id: Long): Stock? =
        withTransaction {
            findByIdInternal(id)
        }

    override fun findByCode(code: String): Stock? =
        withTransaction {
            Stocks.selectAll()
                .where { Stocks.code eq code }
                .singleOrNull()
                ?.toStock()
        }

    override fun existsByCode(code: String): Boolean =
        withTransaction {
            Stocks.selectAll()
                .where { Stocks.code eq code }
                .limit(1)
                .any()
        }

    override fun save(stock: Stock): Stock =
        withTransaction {
            val stockId = Stocks.insertAndGetId {
                it[code] = stock.code
                it[name] = stock.name
                it[description] = stock.description
                it[currentPrice] = stock.currentPrice
                it[previousClose] = stock.previousClose
                it[listed] = stock.listed
                it[createdAt] = stock.createdAt
                it[updatedAt] = stock.updatedAt
            }.value

            findByIdInternal(stockId)
                ?: error("생성된 종목을 조회할 수 없습니다.")
        }

    override fun update(stock: Stock): Stock =
        withTransaction {
            val updatedCount = Stocks.update({
                Stocks.id eq stock.id
            }) {
                it[name] = stock.name
                it[description] = stock.description
                it[listed] = stock.listed
                it[updatedAt] = stock.updatedAt
            }

            if (updatedCount == 0) {
                error("수정할 종목을 찾을 수 없습니다. stockId=${stock.id}")
            }

            findByIdInternal(stock.id)
                ?: error("수정된 종목을 조회할 수 없습니다.")
        }

//    override fun deleteById(id: Long): Boolean = withTransaction {
//        Stocks.deleteWhere { Stocks.id eq id } > 0
//    }

    private fun findByIdInternal(id: Long): Stock? =
        Stocks.selectAll()
            .where { Stocks.id eq id }
            .singleOrNull()
            ?.toStock()

    private fun ResultRow.toStock() = Stock(
        id = this[Stocks.id].value,
        code = this[Stocks.code],
        name = this[Stocks.name],
        description = this[Stocks.description],
        currentPrice = this[Stocks.currentPrice],
        previousClose = this[Stocks.previousClose],
        listed = this[Stocks.listed],
        createdAt = this[Stocks.createdAt],
        updatedAt = this[Stocks.updatedAt],
    )
}