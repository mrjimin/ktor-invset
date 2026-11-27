package io.github.mrjimin.invest.domain.trading.service

import io.github.mrjimin.invest.common.config.TradingFeeConfig
import io.github.mrjimin.invest.domain.trading.model.OrderSide
import java.math.BigDecimal
import java.math.RoundingMode

data class TradingFeeResult(
    val totalAmount: Long,
    val feeRate: BigDecimal,
    val taxRate: BigDecimal,
    val feeAmount: Long,
    val taxAmount: Long,
    val settlementAmount: Long
)

class TradingFeeCalculator {

    fun calculate(
        side: OrderSide,
        price: Long,
        quantity: Int
    ): TradingFeeResult {
        val feeRate = when (side) {
            OrderSide.BUY -> TradingFeeConfig.BUY_FEE_RATE
            OrderSide.SELL -> TradingFeeConfig.SELL_FEE_RATE
        }

        val taxRate = when (side) {
            OrderSide.BUY -> BigDecimal.ZERO
            OrderSide.SELL -> TradingFeeConfig.SELL_TAX_RATE
        }

        return calculate(
            side = side,
            price = price,
            quantity = quantity,
            feeRate = feeRate,
            taxRate = taxRate
        )
    }

    fun calculate(
        side: OrderSide,
        price: Long,
        quantity: Int,
        feeRate: BigDecimal,
        taxRate: BigDecimal
    ): TradingFeeResult {
        require(price > 0L)
        require(quantity > 0)
        require(feeRate >= BigDecimal.ZERO)
        require(taxRate >= BigDecimal.ZERO)

        if (side == OrderSide.BUY) {
            require(taxRate.compareTo(BigDecimal.ZERO) == 0)
        }

        val totalAmount = Math.multiplyExact(
            price,
            quantity.toLong()
        )

        val feeAmount = calculateRate(
            totalAmount,
            feeRate
        )

        val taxAmount = calculateRate(
            totalAmount,
            taxRate
        )

        val settlementAmount = when (side) {
            OrderSide.BUY -> Math.addExact(
                totalAmount,
                feeAmount
            )

            OrderSide.SELL -> Math.subtractExact(
                Math.subtractExact(
                    totalAmount,
                    feeAmount
                ),
                taxAmount
            )
        }

        require(settlementAmount >= 0L) {
            "정산 금액이 음수입니다."
        }

        return TradingFeeResult(
            totalAmount = totalAmount,
            feeRate = feeRate,
            taxRate = taxRate,
            feeAmount = feeAmount,
            taxAmount = taxAmount,
            settlementAmount = settlementAmount
        )
    }

    private fun calculateRate(
        amount: Long,
        rate: BigDecimal
    ): Long {
        return BigDecimal.valueOf(amount)
            .multiply(rate)
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
    }
}
