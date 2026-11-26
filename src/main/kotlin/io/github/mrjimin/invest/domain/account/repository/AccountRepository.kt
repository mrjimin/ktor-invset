package io.github.mrjimin.invest.domain.account.repository

import io.github.mrjimin.invest.domain.account.model.Account
import kotlinx.datetime.LocalDateTime

interface AccountRepository {
    fun findByUserIdForUpdate(userId: Long): Account?

    fun updateBalances(
        userId: Long,
        balance: Long,
        reservedBalance: Long,
        updatedAt: LocalDateTime
    ): Boolean

    fun findByUserId(userId: Long): Account?

    fun save(account: Account): Account
}