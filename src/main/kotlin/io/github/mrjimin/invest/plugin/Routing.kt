package io.github.mrjimin.invest.plugin

import io.github.mrjimin.invest.domain.account.route.accountRoute
import io.github.mrjimin.invest.domain.account.service.AccountService
import io.github.mrjimin.invest.domain.auth.route.authRoute
import io.github.mrjimin.invest.domain.auth.service.AuthService
import io.github.mrjimin.invest.domain.stock.route.stockRoute
import io.github.mrjimin.invest.domain.stock.service.StockService
import io.github.mrjimin.invest.domain.user.route.userRoute
import io.github.mrjimin.invest.domain.user.service.UserService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userService by inject<UserService>()
    val authService by inject<AuthService>()
    val accountService by inject<AccountService>()
    val stockService by inject<StockService>()

    routing {
        get("/health") { call.respondText("OK") }

        route("/api/v1") {
            authRoute(authService)
            userRoute(userService)
            accountRoute(accountService)
            stockRoute(stockService)
        }
    }
}
