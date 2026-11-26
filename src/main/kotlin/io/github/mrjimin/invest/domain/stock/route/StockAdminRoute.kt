package io.github.mrjimin.invest.domain.stock.route

import io.github.mrjimin.invest.common.extension.requireLongParameter
import io.github.mrjimin.invest.common.response.ApiResponse
import io.github.mrjimin.invest.common.security.role.authorized
import io.github.mrjimin.invest.domain.stock.dto.CreateStockRequest
import io.github.mrjimin.invest.domain.stock.dto.UpdateStockRequest
import io.github.mrjimin.invest.domain.stock.dto.toResponse
import io.github.mrjimin.invest.domain.stock.service.StockService
import io.github.mrjimin.invest.domain.user.model.UserRole
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.stockAdminRoute(
    stockService: StockService
) {
    authorized(UserRole.ADMIN) {
        route("/admin/stocks") {
            post {
                val request = call.receive<CreateStockRequest>()
                val stock = stockService.createStock(request)

                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse.success(stock.toResponse(), "종목이 등록되었습니다.")
                )
            }

            patch("/{id}") {
                val id = call.requireLongParameter("id")
                val request = call.receive<UpdateStockRequest>()
                val stock = stockService.updateStock(id, request)

                call.respond(
                    ApiResponse.success(stock.toResponse(), "종목이 수정되었습니다.")
                )
            }

            delete("/{id}") {
                val id = call.requireLongParameter("id")
                stockService.deleteStock(id)

                call.respond(ApiResponse.success("종목이 삭제되었습니다."))
            }
        }
    }
}