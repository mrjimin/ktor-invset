//package io.github.mrjimin.invest.domain.stock.route
//
//import io.github.mrjimin.invest.common.exception.ApiErrorCode
//import io.github.mrjimin.invest.common.exception.ApiException
//import io.github.mrjimin.invest.common.response.ApiResponse
//import io.github.mrjimin.invest.common.security.role.admin
//import io.github.mrjimin.invest.common.security.role.authenticated
//import io.github.mrjimin.invest.common.security.role.authorized
//import io.github.mrjimin.invest.domain.stock.dto.CreateStockPriceRequest
//import io.github.mrjimin.invest.domain.stock.dto.toResponse
//import io.github.mrjimin.invest.domain.stock.service.StockPriceService
//import io.github.mrjimin.invest.domain.user.model.UserRole
//import io.ktor.http.*
//import io.ktor.server.request.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import kotlinx.datetime.LocalDate
//
//fun Route.stockPriceRoute(
//    stockPriceService: StockPriceService
//) {
//    route("/stocks/{stockId}/prices") {
//        authorized(UserRole.ADMIN) {
//            post {
//                val stockId = call.parameters["stockId"]
//                    ?.toLongOrNull()
//                    ?: throw ApiException(ApiErrorCode.INVALID_REQUEST)
//
//                val request = call.receive<CreateStockPriceRequest>()
//
//                val stockPrice = stockPriceService.createStockPrice(
//                    stockId,
//                    request
//                )
//
//                call.respond(
//                    HttpStatusCode.Created,
//                    ApiResponse.success(
//                        stockPrice.toResponse(),
//                        "시세가 등록되었습니다."
//                    )
//                )
//            }
//        }
//
//        authenticated {
//            get {
//                val stockId = call.parameters["stockId"]
//                    ?.toLongOrNull()
//                    ?: throw ApiException(ApiErrorCode.INVALID_REQUEST)
//
//                val from = call.request.queryParameters["from"]
//                    ?.let {
//                        try {
//                            LocalDate.parse(it)
//                        } catch (_: IllegalArgumentException) {
//                            throw ApiException(ApiErrorCode.INVALID_REQUEST)
//                        }
//                    }
//                    ?: throw ApiException(ApiErrorCode.INVALID_REQUEST)
//
//                val to = call.request.queryParameters["to"]
//                    ?.let {
//                        try {
//                            LocalDate.parse(it)
//                        } catch (_: IllegalArgumentException) {
//                            throw ApiException(ApiErrorCode.INVALID_REQUEST)
//                        }
//                    }
//                    ?: throw ApiException(ApiErrorCode.INVALID_REQUEST)
//
//                val prices = stockPriceService.getStockPrices(
//                    stockId,
//                    from,
//                    to
//                ).map { it.toResponse() }
//
//                call.respond(
//                    ApiResponse.success(prices)
//                )
//            }
//
//            get("/latest") {
//                val stockId = call.parameters["stockId"]
//                    ?.toLongOrNull()
//                    ?: throw ApiException(ApiErrorCode.INVALID_REQUEST)
//
//                val price = stockPriceService.getLatestStockPrice(stockId)
//
//                call.respond(
//                    ApiResponse.success(price?.toResponse())
//                )
//            }
//
//            get("/{date}") {
//                val stockId = call.parameters["stockId"]
//                    ?.toLongOrNull()
//                    ?: throw ApiException(ApiErrorCode.INVALID_REQUEST)
//
//                val date = call.parameters["date"]
//                    ?.let {
//                        try {
//                            LocalDate.parse(it)
//                        } catch (_: IllegalArgumentException) {
//                            throw ApiException(ApiErrorCode.INVALID_REQUEST)
//                        }
//                    }
//                    ?: throw ApiException(ApiErrorCode.INVALID_REQUEST)
//
//                val price = stockPriceService.getStockPrice(
//                    stockId,
//                    date
//                )
//
//                call.respond(
//                    ApiResponse.success(price.toResponse())
//                )
//            }
//        }
//    }
//}
