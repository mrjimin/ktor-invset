package io.github.mrjimin.invest.domain.user.service

import io.github.mrjimin.invest.domain.user.dto.CreateUserRequest
import io.github.mrjimin.invest.domain.user.model.User

interface UserService {
    fun getAllUsers(): List<User>
    fun getUserById(id: Long): User
    fun getUserByUsername(username: String): User

    fun registerUser(request: CreateUserRequest): User

    fun deleteUser(id: Long)
}