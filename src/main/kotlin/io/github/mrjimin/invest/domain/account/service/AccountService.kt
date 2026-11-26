package io.github.mrjimin.invest.domain.account.service

import io.github.mrjimin.invest.domain.account.model.Account

interface AccountService {
    fun createAccount(userId: Long): Account
    fun getAccountByUserId(userId: Long): Account
}