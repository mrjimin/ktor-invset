package io.github.mrjimin.invest.domain.account.repository

import io.github.mrjimin.invest.common.database.dbQuery
import io.github.mrjimin.invest.domain.account.model.Account
import io.github.mrjimin.invest.domain.account.repository.table.Accounts
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll

class AccountRepositoryImpl : AccountRepository {
    override suspend fun findByUserId(userId: Long): Account? = dbQuery {
        Accounts.selectAll()
            .where { Accounts.userId eq userId }
            .singleOrNull()
            ?.toAccount()
    }

    override suspend fun save(account: Account): Account = dbQuery {
        val accountId = Accounts.insertAndGetId {
            it[userId] = account.userId
            it[initialBalance] = account.initialBalance
            it[balance] = account.balance
            it[reservedBalance] = account.reservedBalance
            it[createdAt] = account.createdAt
            it[updatedAt] = account.updatedAt
        }.value

        Accounts.selectAll()
            .where { Accounts.id eq accountId }
            .singleOrNull()
            ?.toAccount()
            ?: error("생성된 계좌를 조회할 수 없습니다.")
    }

    private fun ResultRow.toAccount() = Account(
        id = this[Accounts.id].value,
        userId = this[Accounts.userId].value,
        initialBalance = this[Accounts.initialBalance],
        balance = this[Accounts.balance],
        reservedBalance = this[Accounts.reservedBalance],
        createdAt = this[Accounts.createdAt],
        updatedAt = this[Accounts.updatedAt],
    )
}