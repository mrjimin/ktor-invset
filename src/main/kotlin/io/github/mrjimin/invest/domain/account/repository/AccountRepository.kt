package io.github.mrjimin.invest.domain.account.repository

import io.github.mrjimin.invest.domain.account.model.Account

interface AccountRepository {
    suspend fun findByUserId(userId: Long): Account?
    suspend fun save(account: Account): Account
}