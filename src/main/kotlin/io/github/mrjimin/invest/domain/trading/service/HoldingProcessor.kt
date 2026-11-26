package io.github.mrjimin.invest.domain.trading.service

import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.domain.trading.model.Holding
import io.github.mrjimin.invest.domain.trading.repository.HoldingRepository
import kotlinx.datetime.LocalDateTime

internal class HoldingProcessor(
    private val holdingRepository: HoldingRepository
) {

    fun buy(
        userId: Long,
        stockId: Long,
        quantity: Int,
        price: Long,
        now: LocalDateTime
    ) {
        require(quantity > 0)
        require(price > 0)

        val existing = holdingRepository.findByUserAndStockForUpdate(
            userId,
            stockId
        )

        if (existing == null) {
            holdingRepository.save(
                Holding(
                    id = 0L,
                    userId = userId,
                    stockId = stockId,
                    quantity = quantity,
                    averagePrice = price,
                    updatedAt = now
                )
            )
            return
        }

        val newQuantity = Math.addExact(
            existing.quantity,
            quantity
        )

        val oldAmount = Math.multiplyExact(
            existing.quantity.toLong(),
            existing.averagePrice
        )

        val purchaseAmount = Math.multiplyExact(
            quantity.toLong(),
            price
        )

        val newAveragePrice = Math.addExact(
            oldAmount,
            purchaseAmount
        ) / newQuantity

        check(
            holdingRepository.update(
                existing.id,
                newQuantity,
                newAveragePrice,
                now
            )
        ) {
            "보유 주식 갱신에 실패했습니다. holdingId=${existing.id}"
        }
    }

    fun sell(
        holding: Holding,
        quantity: Int,
        now: LocalDateTime
    ) {
        require(quantity > 0)

        if (holding.quantity < quantity) {
            throw ApiException(ApiErrorCode.INSUFFICIENT_HOLDINGS)
        }

        val remaining = Math.subtractExact(
            holding.quantity,
            quantity
        )

        if (remaining == 0) {
            check(
                holdingRepository.deleteByUserAndStock(
                    holding.userId,
                    holding.stockId
                )
            ) {
                "보유 주식 삭제에 실패했습니다. holdingId=${holding.id}"
            }
            return
        }

        check(
            holdingRepository.update(
                holding.id,
                remaining,
                holding.averagePrice,
                now
            )
        ) {
            "보유 주식 갱신에 실패했습니다. holdingId=${holding.id}"
        }
    }
}
