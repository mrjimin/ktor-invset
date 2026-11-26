package io.github.mrjimin.invest.domain.trading.route

import io.github.mrjimin.invest.common.extension.jwtPrincipal
import io.github.mrjimin.invest.common.response.ApiResponse
import io.github.mrjimin.invest.common.security.role.authenticated
import io.github.mrjimin.invest.domain.trading.service.TradingQueryService
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.holdingRoute(
    tradingQueryService: TradingQueryService
) {
    authenticated {
        route("/holdings") {
            get {
                val principal = call.jwtPrincipal()
                val holdings = tradingQueryService.getMyHoldings(principal.userId)

                call.respond(
                    ApiResponse.success(holdings)
                )
            }
        }
    }
}
