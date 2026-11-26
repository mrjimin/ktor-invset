package io.github.mrjimin.invest.domain.user.service

import io.github.mrjimin.invest.domain.user.dto.CreateUserRequest
import io.github.mrjimin.invest.domain.user.model.User

interface UserService {
    suspend fun getAllUsers(): List<User>
    suspend fun getUserById(id: Long): User
    suspend fun getUserByUsername(username: String): User

    suspend fun registerUser(request: CreateUserRequest): User

    suspend fun deleteUser(id: Long)
}