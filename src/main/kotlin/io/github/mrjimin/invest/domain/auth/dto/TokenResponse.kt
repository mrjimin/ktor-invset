package io.github.mrjimin.invest.domain.auth.dto

import io.github.mrjimin.invest.domain.user.dto.UserResponse
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
)