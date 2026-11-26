package io.github.mrjimin.invest.domain.account.route

import io.github.mrjimin.invest.common.extension.jwtPrincipal
import io.github.mrjimin.invest.common.response.ApiResponse
import io.github.mrjimin.invest.common.security.role.authenticated
import io.github.mrjimin.invest.domain.account.dto.toResponse
import io.github.mrjimin.invest.domain.account.service.AccountService
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.accountRoute(
    accountService: AccountService
) {
    authenticated {
        route("/accounts") {
            get("/me") {
                val principal = call.jwtPrincipal()
                val account = accountService.getAccountByUserId(principal.userId)
                call.respond(ApiResponse.success(account.toResponse()))
            }
        }
    }
}