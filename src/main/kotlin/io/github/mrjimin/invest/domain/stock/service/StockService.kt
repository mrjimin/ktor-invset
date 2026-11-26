package io.github.mrjimin.invest.domain.stock.service

import io.github.mrjimin.invest.domain.stock.dto.CreateStockRequest
import io.github.mrjimin.invest.domain.stock.dto.UpdateStockRequest
import io.github.mrjimin.invest.domain.stock.model.Stock

interface StockService {
    fun getAllStocks(onlyListed: Boolean = true): List<Stock>
    fun getStockById(id: Long): Stock

    fun createStock(request: CreateStockRequest): Stock

    fun updateStock(id: Long, request: UpdateStockRequest): Stock

    fun deleteStock(id: Long)
}