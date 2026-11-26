package io.github.mrjimin.invest.domain.account.model

import kotlinx.datetime.LocalDateTime

data class Account(
    val id: Long,
    val userId: Long,
    val initialBalance: Long,
    val balance: Long,
    val reservedBalance: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    val availableBalance: Long
        get() = balance - reservedBalance
}