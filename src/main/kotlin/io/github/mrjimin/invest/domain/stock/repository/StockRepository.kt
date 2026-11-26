package io.github.mrjimin.invest.domain.stock.repository

import io.github.mrjimin.invest.domain.stock.model.Stock

interface StockRepository {
    suspend fun findAll(onlyListed: Boolean = true): List<Stock>
    suspend fun findById(id: Long): Stock?
    suspend fun findByCode(code: String): Stock?

    suspend fun existsByCode(code: String): Boolean

    suspend fun save(stock: Stock): Stock

    suspend fun update(stock: Stock): Stock

    suspend fun deleteById(id: Long): Boolean
}