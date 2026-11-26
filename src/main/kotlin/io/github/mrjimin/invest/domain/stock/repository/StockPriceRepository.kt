package io.github.mrjimin.invest.domain.stock.repository

import io.github.mrjimin.invest.domain.stock.model.StockPrice
import kotlinx.datetime.LocalDate

interface StockPriceRepository {

    fun findByStockIdAndDate(
        stockId: Long,
        date: LocalDate
    ): StockPrice?

    fun findAllByStockIdAndDateBetween(
        stockId: Long,
        from: LocalDate,
        to: LocalDate
    ): List<StockPrice>

    fun findLatestByStockId(
        stockId: Long
    ): StockPrice?

    fun save(stockPrice: StockPrice): StockPrice
}