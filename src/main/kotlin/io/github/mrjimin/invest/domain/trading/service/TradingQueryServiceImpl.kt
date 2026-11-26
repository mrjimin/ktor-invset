package io.github.mrjimin.invest.domain.trading.service

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.domain.trading.dto.HoldingResponse
import io.github.mrjimin.invest.domain.trading.dto.TransactionResponse
import io.github.mrjimin.invest.domain.trading.dto.toResponse
import io.github.mrjimin.invest.domain.trading.repository.HoldingRepository
import io.github.mrjimin.invest.domain.trading.repository.TransactionRepository

class TradingQueryServiceImpl(
    private val holdingRepository: HoldingRepository,
    private val transactionRepository: TransactionRepository
) : TradingQueryService {

    override fun getMyHoldings(userId: Long): List<HoldingResponse> =
        withTransaction {
            holdingRepository.findAllByUserId(userId)
                .map { it.toResponse() }
        }

    override fun getMyTransactions(userId: Long): List<TransactionResponse> =
        withTransaction {
            transactionRepository.findAllByUserId(userId)
                .map { it.toResponse() }
        }
}
