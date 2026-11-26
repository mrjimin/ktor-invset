package io.github.mrjimin.invest.plugin

import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.response.ApiResponse
import io.github.mrjimin.invest.common.security.jwt.JwtPrincipal
import io.github.mrjimin.invest.common.security.jwt.JwtProvider
import io.github.mrjimin.invest.domain.user.model.UserRole
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

const val JWT_AUTH = "jwt-auth"

fun Application.configureSecurity() {
    val jwtProvider by inject<JwtProvider>()

//    val jwtAuth = jwt<JwtPrincipal>(JWT_AUTH) {
//        verifier(jwtProvider.verifier)
//
//        validate { credential ->
//            val payload = credential.payload
//
//            if (payload.getClaim("type").asString() != JwtProvider.TOKEN_TYPE_ACCESS) {
//                return@validate null
//            }
//
//            val userId = payload.subject?.toLongOrNull() ?: return@validate null
//            val username = payload.getClaim("username").asString() ?: return@validate null
//            val role = runCatching {
//                UserRole.valueOf(payload.getClaim("role").asString())
//            }.getOrNull() ?: return@validate null
//
//            JwtPrincipal(userId, username, role)
//        }
//    }

    authentication {
        jwt(JWT_AUTH) {
            verifier(jwtProvider.verifier)

            validate { credential ->
                val payload = credential.payload

                if (payload.getClaim("type").asString() != JwtProvider.TOKEN_TYPE_ACCESS) {
                    return@validate null
                }

                val userId = payload.subject?.toLongOrNull() ?: return@validate null
                val username = payload.getClaim("username").asString() ?: return@validate null
                val role = runCatching {
                    UserRole.valueOf(payload.getClaim("role").asString())
                }.getOrNull() ?: return@validate null

                JwtPrincipal(userId, username, role)
            }

            challenge { _, _ ->
                call.respond(
                    ApiErrorCode.UNAUTHORIZED.status,
                    ApiResponse.failure(ApiErrorCode.UNAUTHORIZED.message)
                )
            }
        }
    }
}