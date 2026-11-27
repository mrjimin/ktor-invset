package io.github.mrjimin.invest.domain.trading.route

import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.common.extension.jwtPrincipal
import io.github.mrjimin.invest.common.response.ApiResponse
import io.github.mrjimin.invest.common.security.role.authenticated
import io.github.mrjimin.invest.common.time.TimeProvider
import io.github.mrjimin.invest.domain.trading.dto.CreateOrderRequest
import io.github.mrjimin.invest.domain.trading.service.OrderService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.orderRoute(
    orderService: OrderService
) {
    authenticated {
        route("/orders") {
            post {
                val principal = call.jwtPrincipal()
                val request = call.receive<CreateOrderRequest>()
                val order = orderService.placeOrder(principal.userId, request)

                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse.success(
                        order,
                        "주문이 접수되었습니다."
                    )
                )
            }

            get {
                val principal = call.jwtPrincipal()
                val orders = orderService.getMyOrders(principal.userId)

                call.respond(
                    ApiResponse.success(orders)
                )
            }

            delete("/{orderId}") {
                val orderId = call.parameters["orderId"]?.toLongOrNull()
                    ?: throw ApiException(
                        ApiErrorCode.INVALID_REQUEST,
                        "올바른 주문 ID가 아닙니다."
                    )

                val principal = call.jwtPrincipal()
                val response = orderService.cancelOrder(principal.userId, orderId)

                call.respond(
                    ApiResponse.success(
                        response,
                        "주문이 취소되었습니다."
                    )
                )
            }

            get("/pending") {

            }
        }
    }

    val timeProvider by inject<TimeProvider>()
    orderAdminRoute(orderService, timeProvider)
}
