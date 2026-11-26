package io.github.mrjimin.invest.domain.account.service

import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.domain.account.config.AccountPolicy
import io.github.mrjimin.invest.domain.account.model.Account
import io.github.mrjimin.invest.domain.account.repository.AccountRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class AccountServiceImpl(
    private val accountRepository: AccountRepository
) : AccountService {

    override suspend fun createAccount(userId: Long): Account {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val account = Account(
            id = 0L,
            userId = userId,
            initialBalance = AccountPolicy.INITIAL_BALANCE,
            balance = AccountPolicy.INITIAL_BALANCE,
            reservedBalance = 0L,
            createdAt = now,
            updatedAt = now,
        )
        return accountRepository.save(account)
    }

    override suspend fun getAccountByUserId(userId: Long): Account =
        accountRepository.findByUserId(userId)
            ?: throw ApiException(ApiErrorCode.ACCOUNT_NOT_FOUND)
}