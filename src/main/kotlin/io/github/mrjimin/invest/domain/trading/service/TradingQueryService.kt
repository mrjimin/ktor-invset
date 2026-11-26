package io.github.mrjimin.invest.domain.trading.service

import io.github.mrjimin.invest.domain.trading.dto.HoldingResponse
import io.github.mrjimin.invest.domain.trading.dto.TransactionResponse

interface TradingQueryService {
    fun getMyHoldings(userId: Long): List<HoldingResponse>
    fun getMyTransactions(userId: Long): List<TransactionResponse>
}