package io.github.mrjimin.invest.domain.trading.route

import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.common.extension.jwtPrincipal
import io.github.mrjimin.invest.common.response.ApiResponse
import io.github.mrjimin.invest.common.security.role.authorized
import io.github.mrjimin.invest.common.time.TimeProvider
import io.github.mrjimin.invest.domain.trading.service.OrderService
import io.github.mrjimin.invest.domain.user.model.UserRole
import io.github.mrjimin.invest.plugin.JWT_AUTH
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class SetTimeRequest(
    val kstIsoTime: String?
)

fun Route.orderAdminRoute(
    orderService: OrderService,
    timeProvider: TimeProvider
) {
    authorized(UserRole.ADMIN) {
        route("/admin/orders") {

            post("/execute-pending") {
                val count = orderService.executePendingOrders()

                call.respond(
                    ApiResponse.success(
                        mapOf("executedCount" to count),
                        "예약 주문 실행이 완료되었습니다."
                    )
                )
            }

            post("/time") {
                val request = call.receive<SetTimeRequest>()
                val parsed = try {
                    request.kstIsoTime?.let {
                        ZonedDateTime.parse(it)
                    }
                } catch (_: Exception) {
                    throw ApiException(
                        ApiErrorCode.INVALID_REQUEST,
                        "올바른 ISO-8601 날짜 형식이 아닙니다."
                    )
                }

                timeProvider.setFixedTime(parsed)

                call.respond(
                    ApiResponse.success(
                        timeProvider.nowKst().toString(),
                        "서버 시간이 변경되었습니다."
                    )
                )
            }

            // 실시간 시각 복귀
            delete("/time") {
                timeProvider.setFixedTime(null)

                call.respond(
                    ApiResponse.success(
                        timeProvider.nowKst().toString(),
                        "실시간 서버 시각으로 복귀했습니다."
                    )
                )
            }
        }
    }
}
