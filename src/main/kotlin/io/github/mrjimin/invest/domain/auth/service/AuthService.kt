package io.github.mrjimin.invest.domain.auth.service

import io.github.mrjimin.invest.domain.auth.dto.LoginRequest
import io.github.mrjimin.invest.domain.auth.dto.TokenResponse
import io.github.mrjimin.invest.domain.user.dto.CreateUserRequest

interface AuthService {
    suspend fun signup(request: CreateUserRequest): TokenResponse
    suspend fun login(request: LoginRequest): TokenResponse
    suspend fun refresh(refreshToken: String): TokenResponse
}
