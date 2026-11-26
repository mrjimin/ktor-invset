package io.github.mrjimin.invest.plugin

import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.common.response.ApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ApiException> { call, cause ->
            call.respond(
                cause.errorCode.status,
                ApiResponse.failure(cause.message)
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse.failure(cause.message ?: "잘못된 요청입니다.")
            )
        }

        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse.failure("서버 내부 오류가 발생했습니다.")
            )
        }
    }
}