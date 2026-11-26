package io.github.mrjimin.invest.domain.stock.service

import io.github.mrjimin.invest.domain.stock.dto.CreateStockPriceRequest
import io.github.mrjimin.invest.domain.stock.model.StockPrice
import kotlinx.datetime.LocalDate

interface StockPriceService {

    fun getStockPrice(
        stockId: Long,
        date: LocalDate
    ): StockPrice

    fun getStockPrices(
        stockId: Long,
        from: LocalDate,
        to: LocalDate
    ): List<StockPrice>

    fun getLatestStockPrice(stockId: Long): StockPrice?

    fun createStockPrice(
        stockId: Long,
        request: CreateStockPriceRequest
    ): StockPrice
}
