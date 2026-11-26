package io.github.mrjimin.invest.domain.auth.service

import io.github.mrjimin.invest.domain.auth.dto.LoginRequest
import io.github.mrjimin.invest.domain.auth.dto.TokenResponse
import io.github.mrjimin.invest.domain.user.dto.CreateUserRequest

interface AuthService {
    fun signup(request: CreateUserRequest): TokenResponse
    fun login(request: LoginRequest): TokenResponse
    fun refresh(refreshToken: String): TokenResponse
}
