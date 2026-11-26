package io.github.mrjimin.invest.domain.auth.repository

import io.github.mrjimin.invest.domain.auth.model.RefreshToken
import kotlinx.datetime.LocalDateTime

interface RefreshTokenRepository {

    fun save(refreshToken: RefreshToken)

    fun rotate(
        userId: Long,
        tokenHash: String,
        now: LocalDateTime,
        newToken: RefreshToken
    ): Boolean

    fun deleteByUserId(userId: Long): Int

    fun deleteExpiredTokens(now: LocalDateTime): Int
}