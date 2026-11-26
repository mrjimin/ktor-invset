package io.github.mrjimin.invest.domain.user.route

import io.github.mrjimin.invest.common.extension.requireLongParameter
import io.github.mrjimin.invest.common.response.ApiResponse
import io.github.mrjimin.invest.common.security.role.authorized
import io.github.mrjimin.invest.domain.user.dto.toResponse
import io.github.mrjimin.invest.domain.user.model.UserRole
import io.github.mrjimin.invest.domain.user.service.UserService
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userAdminRoute(
    userService: UserService
) {
    authorized(UserRole.ADMIN) {
        route("/admin/users") {
            get {
                val users = userService.getAllUsers()

                call.respond(
                    ApiResponse.success(
                        users.map { it.toResponse() }
                    )
                )
            }

            get("/{id}") {
                val id = call.requireLongParameter("id")
                val user = userService.getUserById(id)

                call.respond(
                    ApiResponse.success(user.toResponse())
                )
            }

            delete("/{id}") {
                val id = call.requireLongParameter("id")
                userService.deleteUser(id)

                call.respond(
                    ApiResponse.success("사용자가 삭제되었습니다.")
                )
            }
        }
    }
}