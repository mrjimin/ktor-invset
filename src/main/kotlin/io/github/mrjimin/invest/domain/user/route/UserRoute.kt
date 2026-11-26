package io.github.mrjimin.invest.domain.user.route

import io.github.mrjimin.invest.common.response.ApiResponse
import io.github.mrjimin.invest.common.security.jwt.JwtPrincipal
import io.github.mrjimin.invest.common.security.role.authenticated
import io.github.mrjimin.invest.domain.user.dto.toResponse
import io.github.mrjimin.invest.domain.user.service.UserService
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoute(
    userService: UserService
) {
    authenticated {
        route("/users") {
            get("/me") {
                val principal = call.principal<JwtPrincipal>()
                    ?: return@get

                val user = userService.getUserById(principal.userId)

                call.respond(
                    ApiResponse.success(user.toResponse())
                )
            }
        }
    }
    userAdminRoute(userService)
}