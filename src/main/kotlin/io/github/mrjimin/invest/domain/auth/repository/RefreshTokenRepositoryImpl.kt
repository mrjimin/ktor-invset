package io.github.mrjimin.invest.domain.auth.repository

import io.github.mrjimin.invest.common.database.dbQuery
import io.github.mrjimin.invest.domain.auth.model.RefreshToken
import io.github.mrjimin.invest.domain.auth.repository.table.RefreshTokens
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.*

class RefreshTokenRepositoryImpl : RefreshTokenRepository {

    override suspend fun save(refreshToken: RefreshToken): Unit = dbQuery {
        RefreshTokens.insert {
            it[userId] = refreshToken.userId
            it[tokenHash] = refreshToken.tokenHash
            it[expiresAt] = refreshToken.expiresAt
            it[createdAt] = refreshToken.createdAt
        }
    }

    override suspend fun rotate(
        userId: Long,
        tokenHash: String,
        now: LocalDateTime,
        newToken: RefreshToken
    ): Boolean = dbQuery {
        val deleted = RefreshTokens.deleteWhere {
            (RefreshTokens.userId eq userId) and
                    (RefreshTokens.tokenHash eq tokenHash) and
                    (RefreshTokens.expiresAt greater now)
        }

        if (deleted != 1) {
            return@dbQuery false
        }

        RefreshTokens.insert {
            it[RefreshTokens.userId] = newToken.userId
            it[RefreshTokens.tokenHash] = newToken.tokenHash
            it[RefreshTokens.expiresAt] = newToken.expiresAt
            it[RefreshTokens.createdAt] = newToken.createdAt
        }

        true
    }

    override suspend fun deleteByUserId(userId: Long): Int = dbQuery {
        RefreshTokens.deleteWhere {
            RefreshTokens.userId eq userId
        }
    }

    override suspend fun deleteExpiredTokens(now: LocalDateTime): Int = dbQuery {
        RefreshTokens.deleteWhere {
            RefreshTokens.expiresAt lessEq now
        }
    }
}