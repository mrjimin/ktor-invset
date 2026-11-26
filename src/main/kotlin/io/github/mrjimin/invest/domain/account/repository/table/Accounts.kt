package io.github.mrjimin.invest.domain.account.repository.table

import io.github.mrjimin.invest.domain.account.config.AccountPolicy
import io.github.mrjimin.invest.domain.user.repository.table.Users
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.datetime

object Accounts : LongIdTable("accounts") {
    val userId = reference("user_id", Users.id).uniqueIndex()
    val initialBalance = long("initial_balance").default(AccountPolicy.INITIAL_BALANCE)
    val balance = long("balance").default(AccountPolicy.INITIAL_BALANCE)
    val reservedBalance = long("reserved_balance").default(0L)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}