package io.github.mrjimin.invest.domain.user.model

import kotlinx.datetime.LocalDateTime

data class User(
    val id: Long,
    val username: String,
    val password: String,
    val nickname: String,
    val type: UserType,
    val role: UserRole,
    val studentNumber: StudentNumber?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)