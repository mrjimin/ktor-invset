package io.github.mrjimin.invest.domain.user.dto

import io.github.mrjimin.invest.domain.user.model.User
import io.github.mrjimin.invest.domain.user.model.UserRole
import io.github.mrjimin.invest.domain.user.model.UserType
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val username: String,
    val nickname: String,
    val type: UserType,
    val role: UserRole,
    val studentNumber: String?,
    val createdAt: String,
    val updatedAt: String,
)

fun User.toResponse() = UserResponse(
    id = id,
    username = username,
    nickname = nickname,
    type = type,
    role = role,
    studentNumber = studentNumber?.value,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString(),
)