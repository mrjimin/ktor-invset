package io.github.mrjimin.invest.domain.stock.repository

import io.github.mrjimin.invest.common.database.dbQuery
import io.github.mrjimin.invest.domain.stock.model.Stock
import io.github.mrjimin.invest.domain.stock.repository.table.Stocks
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class StockRepositoryImpl : StockRepository {

    override suspend fun findAll(onlyListed: Boolean): List<Stock> = dbQuery {
        val query = Stocks.selectAll()
        if (onlyListed) {
            query.where { Stocks.listed eq true }
        }
        query.map { it.toStock() }
    }

    override suspend fun findById(id: Long): Stock? = dbQuery {
        findByIdInternal(id)
    }

    override suspend fun findByCode(code: String): Stock? = dbQuery {
        Stocks.selectAll()
            .where { Stocks.code eq code }
            .singleOrNull()
            ?.toStock()
    }

    override suspend fun existsByCode(code: String): Boolean = dbQuery {
        Stocks.selectAll()
            .where { Stocks.code eq code }
            .limit(1)
            .any()
    }

    override suspend fun save(stock: Stock): Stock = dbQuery {
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

    override suspend fun update(stock: Stock): Stock = dbQuery {
        Stocks.update({ Stocks.id eq stock.id }) {
            it[name] = stock.name
            it[description] = stock.description
            it[listed] = stock.listed
            it[updatedAt] = stock.updatedAt
        }

        findByIdInternal(stock.id)
            ?: error("수정된 종목을 조회할 수 없습니다.")
    }

    override suspend fun deleteById(id: Long): Boolean = dbQuery {
        Stocks.deleteWhere { Stocks.id eq id } > 0
    }

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