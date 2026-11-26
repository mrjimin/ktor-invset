package io.github.mrjimin.invest.domain.auth.repository.table

import io.github.mrjimin.invest.domain.user.repository.table.Users
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.datetime

object RefreshTokens : LongIdTable("refresh_tokens") {
    val userId = reference("user_id", Users.id)
    val tokenHash = varchar("token_hash", 64).uniqueIndex()
    val expiresAt = datetime("expires_at")
    val createdAt = datetime("created_at")

    init {
        index(false, userId)
    }
}
