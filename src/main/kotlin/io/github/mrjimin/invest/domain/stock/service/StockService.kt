package io.github.mrjimin.invest.domain.stock.service

import io.github.mrjimin.invest.domain.stock.dto.CreateStockRequest
import io.github.mrjimin.invest.domain.stock.dto.UpdateStockRequest
import io.github.mrjimin.invest.domain.stock.model.Stock

interface StockService {
    suspend fun getAllStocks(onlyListed: Boolean = true): List<Stock>
    suspend fun getStockById(id: Long): Stock

    suspend fun createStock(request: CreateStockRequest): Stock

    suspend fun updateStock(id: Long, request: UpdateStockRequest): Stock

    suspend fun deleteStock(id: Long)
}