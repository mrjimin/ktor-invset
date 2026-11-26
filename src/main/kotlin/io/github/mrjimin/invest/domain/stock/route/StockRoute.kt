package io.github.mrjimin.invest.domain.stock.route

import io.github.mrjimin.invest.common.extension.requireLongParameter
import io.github.mrjimin.invest.common.response.ApiResponse
import io.github.mrjimin.invest.domain.stock.dto.toResponse
import io.github.mrjimin.invest.domain.stock.service.StockService
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.stockRoute(
    stockService: StockService
) {
    route("/stocks") {
        get {
            val onlyListed = call.request.queryParameters["onlyListed"]?.toBooleanStrictOrNull() ?: true
            val stocks = stockService.getAllStocks(onlyListed)
            call.respond(ApiResponse.success(stocks.map { it.toResponse() }))
        }

        get("/{id}") {
            val id = call.requireLongParameter("id")
            val stock = stockService.getStockById(id)
            call.respond(ApiResponse.success(stock.toResponse()))
        }
    }
    stockAdminRoute(stockService)
}