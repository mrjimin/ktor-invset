package io.github.mrjimin.invest.common.extension

import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.common.security.jwt.JwtPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*

fun ApplicationCall.requireLongParameter(name: String): Long =
    parameters[name]?.toLongOrNull()
        // ?.takeIf { it > 0 }
        ?: throw BadRequestException("Invalid parameter: $name")


fun ApplicationCall.jwtPrincipal(): JwtPrincipal =
    principal<JwtPrincipal>()
        ?: throw ApiException(ApiErrorCode.UNAUTHORIZED)