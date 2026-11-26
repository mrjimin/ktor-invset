package io.github.mrjimin.invest.domain.stock.service

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.common.time.TimeProvider
import io.github.mrjimin.invest.domain.stock.dto.CreateStockPriceRequest
import io.github.mrjimin.invest.domain.stock.model.StockPrice
import io.github.mrjimin.invest.domain.stock.repository.StockPriceRepository
import io.github.mrjimin.invest.domain.stock.repository.StockRepository
import kotlinx.datetime.LocalDate

class StockPriceServiceImpl(
    private val stockPriceRepository: StockPriceRepository,
    private val stockRepository: StockRepository,
    private val timeProvider: TimeProvider
) : StockPriceService {

    override fun getStockPrice(
        stockId: Long,
        date: LocalDate
    ): StockPrice =
        stockPriceRepository.findByStockIdAndDate(stockId, date)
            ?: throw ApiException(ApiErrorCode.INVALID_REQUEST, "해당 날짜의 시세 정보가 없습니다.")

    override fun getStockPrices(
        stockId: Long,
        from: LocalDate,
        to: LocalDate
    ): List<StockPrice> {
        validateDateRange(from, to)

        if (stockRepository.findById(stockId) == null) {
            throw ApiException(ApiErrorCode.STOCK_NOT_FOUND)
        }

        return stockPriceRepository.findAllByStockIdAndDateBetween(
            stockId = stockId,
            from = from,
            to = to
        )
    }

    override fun getLatestStockPrice(stockId: Long): StockPrice? {
        if (stockRepository.findById(stockId) == null) {
            throw ApiException(ApiErrorCode.STOCK_NOT_FOUND)
        }

        return stockPriceRepository.findLatestByStockId(stockId)
    }


    override fun createStockPrice(
        stockId: Long,
        request: CreateStockPriceRequest
    ): StockPrice = withTransaction {

        if (stockRepository.findById(stockId) == null) {
            throw ApiException(ApiErrorCode.STOCK_NOT_FOUND)
        }

        validatePrice(
            request.open,
            request.high,
            request.low,
            request.close
        )

        if (
            stockPriceRepository.findByStockIdAndDate(
                stockId,
                request.date
            ) != null
        ) {
            throw ApiException(ApiErrorCode.INVALID_REQUEST, "해당 날짜의 시세 정보가 이미 존재합니다.")
        }

        stockPriceRepository.save(
            StockPrice(
                id = 0L,
                stockId = stockId,
                date = request.date,
                open = request.open,
                high = request.high,
                low = request.low,
                close = request.close,
                createdAt = timeProvider.nowUtc()
            )
        )
    }


    private fun validateDateRange(
        from: LocalDate,
        to: LocalDate
    ) {
        if (from > to) {
            throw ApiException(ApiErrorCode.INVALID_REQUEST, "시작 날짜는 종료 날짜보다 늦을 수 없습니다.")
        }
    }

    private fun validatePrice(
        open: Long,
        high: Long,
        low: Long,
        close: Long
    ) {
        if (
            open <= 0L ||
            high <= 0L ||
            low <= 0L ||
            close <= 0L
        ) {
            throw ApiException(ApiErrorCode.INVALID_REQUEST, "주가는 0보다 커야 합니다.")
        }

        if (
            high < maxOf(open, low, close) ||
            low > minOf(open, high, close)
        ) {
            throw ApiException(ApiErrorCode.INVALID_REQUEST, "고가와 저가가 시가 및 종가의 범위를 벗어났습니다.")
        }
    }
}
