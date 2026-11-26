package io.github.mrjimin.invest.domain.auth.repository

import io.github.mrjimin.invest.domain.auth.model.RefreshToken
import kotlinx.datetime.LocalDateTime

interface RefreshTokenRepository {

    suspend fun save(refreshToken: RefreshToken)

    suspend fun rotate(
        userId: Long,
        tokenHash: String,
        now: LocalDateTime,
        newToken: RefreshToken
    ): Boolean

    suspend fun deleteByUserId(userId: Long): Int

    suspend fun deleteExpiredTokens(now: LocalDateTime): Int
}