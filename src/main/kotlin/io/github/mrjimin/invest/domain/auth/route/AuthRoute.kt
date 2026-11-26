package io.github.mrjimin.invest.domain.auth.route

import io.github.mrjimin.invest.common.response.ApiResponse
import io.github.mrjimin.invest.domain.auth.dto.LoginRequest
import io.github.mrjimin.invest.domain.auth.dto.RefreshTokenRequest
import io.github.mrjimin.invest.domain.auth.service.AuthService
import io.github.mrjimin.invest.domain.user.dto.CreateUserRequest
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoute(
    authService: AuthService
) {
    route("/auth") {
        post("/signup") {
            val request = call.receive<CreateUserRequest>()
            val result = authService.signup(request)

            call.respond(
                HttpStatusCode.Created,
                ApiResponse.success(result, "회원가입이 완료되었습니다.")
            )
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val result = authService.login(request)

            call.respond(
                ApiResponse.success(result, "로그인에 성공했습니다.")
            )
        }

        post("/refresh") {
            val request = call.receive<RefreshTokenRequest>()


            val result = authService.refresh(request.refreshToken)

            call.respond(
                ApiResponse.success(result, "토큰이 갱신되었습니다.")
            )
        }
    }
}