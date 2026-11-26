package io.github.mrjimin.invest.domain.stock.service

import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.domain.stock.dto.CreateStockRequest
import io.github.mrjimin.invest.domain.stock.dto.UpdateStockRequest
import io.github.mrjimin.invest.domain.stock.model.Stock
import io.github.mrjimin.invest.domain.stock.repository.StockRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class StockServiceImpl(
    private val stockRepository: StockRepository
) : StockService {

    override suspend fun getAllStocks(onlyListed: Boolean): List<Stock> =
        stockRepository.findAll(onlyListed)

    override suspend fun getStockById(id: Long): Stock =
        stockRepository.findById(id)
            ?: throw ApiException(ApiErrorCode.STOCK_NOT_FOUND)

    override suspend fun createStock(request: CreateStockRequest): Stock {
        val code = request.code.trim().uppercase()
        val name = request.name.trim()

        if (code.isBlank() || code.length > 20) {
            throw ApiException(ApiErrorCode.INVALID_REQUEST, "종목 코드는 1~20자여야 합니다.")
        }
        if (name.isBlank() || name.length > 100) {
            throw ApiException(ApiErrorCode.INVALID_REQUEST, "종목명은 1~100자여야 합니다.")
        }
        if (request.initialPrice < 1_000L) {
            throw ApiException(ApiErrorCode.INVALID_REQUEST, "초기 주가는 1,000원 이상이어야 합니다.")
        }
        if (stockRepository.existsByCode(code)) {
            throw ApiException(ApiErrorCode.STOCK_CODE_ALREADY_EXISTS)
        }

        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

        val stock = Stock(
            id = 0L,
            code = code,
            name = name,
            description = request.description?.trim(),
            currentPrice = request.initialPrice,
            previousClose = request.initialPrice,
            listed = true,
            createdAt = now,
            updatedAt = now,
        )

        return stockRepository.save(stock)
    }

    override suspend fun updateStock(id: Long, request: UpdateStockRequest): Stock {
        val existing = getStockById(id)
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

        val updated = existing.copy(
            name = request.name?.trim()?.takeIf { it.isNotBlank() } ?: existing.name,
            description = request.description?.trim() ?: existing.description,
            listed = request.listed ?: existing.listed,
            updatedAt = now,
        )

        return stockRepository.update(updated)
    }

    override suspend fun deleteStock(id: Long) {
        val deleted = stockRepository.deleteById(id)
        if (!deleted) {
            throw ApiException(ApiErrorCode.STOCK_NOT_FOUND)
        }
    }
}