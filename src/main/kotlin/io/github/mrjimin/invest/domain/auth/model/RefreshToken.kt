package io.github.mrjimin.invest.domain.auth.model

import kotlinx.datetime.LocalDateTime

data class RefreshToken(
    val userId: Long,
    val tokenHash: String,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime
)