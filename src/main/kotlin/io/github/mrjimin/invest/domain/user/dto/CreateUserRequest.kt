package io.github.mrjimin.invest.domain.user.dto

import io.github.mrjimin.invest.domain.user.model.UserType
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val username: String,
    val password: String,
    val nickname: String,
    val type: UserType,
    val studentNumber: String? = null
)
