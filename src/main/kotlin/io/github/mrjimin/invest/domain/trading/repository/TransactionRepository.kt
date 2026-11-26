package io.github.mrjimin.invest.domain.trading.repository

import io.github.mrjimin.invest.domain.trading.model.Transaction

interface TransactionRepository {
    fun save(transaction: Transaction): Transaction

    fun findAllByUserId(userId: Long): List<Transaction>
}