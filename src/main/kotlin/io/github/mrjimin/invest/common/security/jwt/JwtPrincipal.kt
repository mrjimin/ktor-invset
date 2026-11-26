package io.github.mrjimin.invest.common.security.jwt

import io.github.mrjimin.invest.domain.user.model.UserRole

data class JwtPrincipal(

    val userId: Long,
    val username: String,
    val role: UserRole
) {
    val isAdmin
        get() = role == UserRole.ADMIN
}