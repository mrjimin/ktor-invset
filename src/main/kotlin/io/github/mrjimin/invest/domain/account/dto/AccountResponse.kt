package io.github.mrjimin.invest.domain.account.dto

import io.github.mrjimin.invest.domain.account.model.Account
import kotlinx.serialization.Serializable

@Serializable
data class AccountResponse(
    val id: Long,
    val userId: Long,
    val initialBalance: Long,
    val balance: Long,
    val reservedBalance: Long,
    val availableBalance: Long,
    val createdAt: String,
    val updatedAt: String,
)

fun Account.toResponse() = AccountResponse(
    id = id,
    userId = userId,
    initialBalance = initialBalance,
    balance = balance,
    reservedBalance = reservedBalance,
    availableBalance = availableBalance,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString(),
)