package io.github.mrjimin.invest.domain.stock.service

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.common.time.TimeProvider
import io.github.mrjimin.invest.domain.stock.dto.CreateStockRequest
import io.github.mrjimin.invest.domain.stock.dto.UpdateStockRequest
import io.github.mrjimin.invest.domain.stock.model.Stock
import io.github.mrjimin.invest.domain.stock.repository.StockRepository

class StockServiceImpl(
    private val stockRepository: StockRepository,
    private val timeProvider: TimeProvider
) : StockService {

    override fun getAllStocks(onlyListed: Boolean): List<Stock> =
        stockRepository.findAll(onlyListed)

    override fun getStockById(id: Long): Stock =
        stockRepository.findById(id)
            ?: throw ApiException(ApiErrorCode.STOCK_NOT_FOUND)

    override fun createStock(request: CreateStockRequest): Stock =
        withTransaction {
            val code = request.code.trim().uppercase()
            val name = request.name.trim()

            validateStock(code, name, request.initialPrice)

            if (stockRepository.existsByCode(code)) {
                throw ApiException(
                    ApiErrorCode.STOCK_CODE_ALREADY_EXISTS
                )
            }

            val now = timeProvider.nowUtc()

            val stock = Stock(
                id = 0L,
                code = code,
                name = name,
                description = request.description
                    ?.trim()
                    ?.takeIf { it.isNotBlank() },
                currentPrice = request.initialPrice,
                previousClose = request.initialPrice,
                listed = true,
                createdAt = now,
                updatedAt = now
            )

            stockRepository.save(stock)
        }

    override fun updateStock(
        id: Long,
        request: UpdateStockRequest
    ): Stock = withTransaction {
        val existing = stockRepository.findById(id)
            ?: throw ApiException(ApiErrorCode.STOCK_NOT_FOUND)

        if (!existing.listed) {
            throw ApiException(ApiErrorCode.STOCK_NOT_LISTED)
        }

        val updated = existing.copy(
            name = request.name
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: existing.name,
            description = request.description
                ?.trim()
                ?: existing.description,
            listed = request.listed ?: true,
            updatedAt = timeProvider.nowUtc()
        )

        stockRepository.update(updated)
    }

    override fun deleteStock(id: Long) {
        withTransaction {
            val existing = stockRepository.findById(id)
                ?: throw ApiException(ApiErrorCode.STOCK_NOT_FOUND)

            if (!existing.listed) {
                throw ApiException(
                    ApiErrorCode.INVALID_REQUEST,
                    "이미 상장 폐지된 종목입니다."
                )
            }

            stockRepository.update(
                existing.copy(
                    listed = false,
                    updatedAt = timeProvider.nowUtc()
                )
            )
        }
    }

    private fun validateStock(
        code: String,
        name: String,
        initialPrice: Long
    ) {
        if (code.isBlank() || code.length > 20) {
            throw ApiException(ApiErrorCode.INVALID_REQUEST, "종목 코드는 1~20자여야 합니다.")
        }

        if (name.isBlank() || name.length > 100) {
            throw ApiException(ApiErrorCode.INVALID_REQUEST, "종목명은 1~100자여야 합니다.")
        }

        if (initialPrice < 1_000L) {
            throw ApiException(ApiErrorCode.INVALID_REQUEST, "초기 주가는 1,000원 이상이어야 합니다.")
        }
    }
}
