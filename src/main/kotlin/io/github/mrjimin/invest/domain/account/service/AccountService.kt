package io.github.mrjimin.invest.domain.account.service

import io.github.mrjimin.invest.domain.account.model.Account

interface AccountService {
    suspend fun createAccount(userId: Long): Account
    suspend fun getAccountByUserId(userId: Long): Account
}