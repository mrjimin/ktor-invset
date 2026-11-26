package io.github.mrjimin.invest.domain.stock.repository

import io.github.mrjimin.invest.domain.stock.model.Stock

interface StockRepository {
    fun findAll(onlyListed: Boolean = true): List<Stock>
    fun findById(id: Long): Stock?
    fun findByCode(code: String): Stock?

    fun existsByCode(code: String): Boolean

    fun save(stock: Stock): Stock

    fun update(stock: Stock): Stock

//    fun deleteById(id: Long): Boolean
}