package io.github.mrjimin.invest.domain.auth.service

import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.common.security.PasswordEncoder
import io.github.mrjimin.invest.common.security.jwt.JwtProvider
import io.github.mrjimin.invest.common.security.jwt.RefreshTokenHasher
import io.github.mrjimin.invest.domain.account.service.AccountService
import io.github.mrjimin.invest.domain.auth.dto.LoginRequest
import io.github.mrjimin.invest.domain.auth.dto.TokenResponse
import io.github.mrjimin.invest.domain.auth.model.RefreshToken
import io.github.mrjimin.invest.domain.auth.repository.RefreshTokenRepository
import io.github.mrjimin.invest.domain.user.dto.CreateUserRequest
import io.github.mrjimin.invest.domain.user.dto.toResponse
import io.github.mrjimin.invest.domain.user.model.User
import io.github.mrjimin.invest.domain.user.service.UserService
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

class AuthServiceImpl(
    private val userService: UserService,
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider,
    private val refreshTokenRepository: RefreshTokenRepository
) : AuthService {

    override suspend fun signup(request: CreateUserRequest): TokenResponse {
        val user = userService.registerUser(request)
        accountService.createAccount(user.id)
        return issueTokens(user)
    }

    override suspend fun login(request: LoginRequest): TokenResponse {
        val user = userService.getUserByUsername(
            request.username.trim()
        )

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw ApiException(ApiErrorCode.INVALID_CREDENTIALS)
        }

        return issueTokens(user)
    }

    override suspend fun refresh(refreshToken: String): TokenResponse {
        val decoded = jwtProvider.verifyRefreshToken(refreshToken)
            ?: throw ApiException(ApiErrorCode.INVALID_REFRESH_TOKEN)

        val userId = decoded.subject?.toLongOrNull()
            ?: throw ApiException(ApiErrorCode.INVALID_REFRESH_TOKEN)

        val user = userService.getUserById(userId)

        val now = Clock.System.now()

        val bundle = createTokenBundle(user, now)

        val rotated = refreshTokenRepository.rotate(
            userId = userId,
            tokenHash = RefreshTokenHasher.hash(refreshToken),
            now = now.toLocalDateTime(TimeZone.UTC),
            newToken = bundle.refreshToken
        )

        if (!rotated) {
            throw ApiException(ApiErrorCode.INVALID_REFRESH_TOKEN)
        }

        return bundle.response
    }

    private suspend fun issueTokens(user: User): TokenResponse {
        val now = Clock.System.now()
        val bundle = createTokenBundle(user, now)

        refreshTokenRepository.save(bundle.refreshToken)

        return bundle.response
    }

    private fun createTokenBundle(
        user: User,
        now: Instant
    ): TokenBundle {
        val accessToken = jwtProvider.createAccessToken(user)
        val refreshToken = jwtProvider.createRefreshToken(user)

        val decoded = jwtProvider.verifyRefreshToken(refreshToken)
            ?: throw IllegalStateException("Invalid generated refresh token")

        val expiresAt = decoded.expiresAt?.time
            ?: throw IllegalStateException("Refresh token expiration is missing")

        val refreshTokenModel = RefreshToken(
            userId = user.id,
            tokenHash = RefreshTokenHasher.hash(refreshToken),
            expiresAt = Instant
                .fromEpochMilliseconds(expiresAt)
                .toLocalDateTime(TimeZone.UTC),
            createdAt = now.toLocalDateTime(TimeZone.UTC)
        )

        val response = TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = user.toResponse()
        )

        return TokenBundle(
            response = response,
            refreshToken = refreshTokenModel
        )
    }

    private data class TokenBundle(
        val response: TokenResponse,
        val refreshToken: RefreshToken
    )
}