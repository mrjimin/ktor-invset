package io.github.mrjimin.invest.common.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.github.mrjimin.invest.common.config.JwtConfig
import io.github.mrjimin.invest.domain.user.model.User
import java.util.*

class JwtProvider(
    private val config: JwtConfig
) {
    private val algorithm = Algorithm.HMAC256(config.secret)

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(config.issuer)
        .withAudience(config.audience)
        .build()

    fun createAccessToken(user: User): String =
        createToken(
            user = user,
            type = TOKEN_TYPE_ACCESS,
            expirationMs = config.accessExpirationMs
        )

    fun createRefreshToken(user: User): String =
        createToken(
            user = user,
            type = TOKEN_TYPE_REFRESH,
            expirationMs = config.refreshExpirationMs
        )

    fun verifyAccessToken(token: String): DecodedJWT? =
        verifyToken(token, TOKEN_TYPE_ACCESS)

    fun verifyRefreshToken(token: String): DecodedJWT? =
        verifyToken(token, TOKEN_TYPE_REFRESH)

    private fun verifyToken(
        token: String,
        expectedType: String
    ): DecodedJWT? = runCatching {
        verifier.verify(token).takeIf {
            it.getClaim("type").asString() == expectedType
        }
    }.getOrNull()

    private fun createToken(
        user: User,
        type: String,
        expirationMs: Long
    ): String {
        val now = System.currentTimeMillis()

        return JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withSubject(user.id.toString())
            .withClaim("type", type)
            .withClaim("username", user.username)
            .withClaim("role", user.role.name)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + expirationMs))
            .sign(algorithm)
    }

    companion object {
        const val TOKEN_TYPE_ACCESS = "access"
        const val TOKEN_TYPE_REFRESH = "refresh"
    }
}