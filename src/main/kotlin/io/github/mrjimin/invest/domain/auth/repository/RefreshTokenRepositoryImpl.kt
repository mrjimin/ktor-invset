package io.github.mrjimin.invest.domain.auth.repository

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.domain.auth.model.RefreshToken
import io.github.mrjimin.invest.domain.auth.repository.table.RefreshTokens
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.*

class RefreshTokenRepositoryImpl : RefreshTokenRepository {

    override fun save(refreshToken: RefreshToken): Unit =
        withTransaction {
            RefreshTokens.insert {
                it[userId] = refreshToken.userId
                it[tokenHash] = refreshToken.tokenHash
                it[expiresAt] = refreshToken.expiresAt
                it[createdAt] = refreshToken.createdAt
            }
        }

    override fun rotate(
        userId: Long,
        tokenHash: String,
        now: LocalDateTime,
        newToken: RefreshToken
    ): Boolean = withTransaction {
        val deleted = RefreshTokens.deleteWhere {
            (RefreshTokens.userId eq userId) and
                    (RefreshTokens.tokenHash eq tokenHash) and
                    (RefreshTokens.expiresAt greater now)
        }

        if (deleted != 1) {
            return@withTransaction false
        }

        RefreshTokens.insert {
            it[RefreshTokens.userId] = newToken.userId
            it[RefreshTokens.tokenHash] = newToken.tokenHash
            it[RefreshTokens.expiresAt] = newToken.expiresAt
            it[RefreshTokens.createdAt] = newToken.createdAt
        }

        true
    }

    override fun deleteByUserId(userId: Long): Int =
        withTransaction {
            RefreshTokens.deleteWhere {
                RefreshTokens.userId eq userId
            }
        }

    override fun deleteExpiredTokens(now: LocalDateTime): Int =
        withTransaction {
            RefreshTokens.deleteWhere {
                RefreshTokens.expiresAt lessEq now
            }
        }
}