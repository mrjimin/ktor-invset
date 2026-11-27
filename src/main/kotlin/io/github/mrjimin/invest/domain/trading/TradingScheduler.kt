package io.github.mrjimin.invest.domain.trading

import io.github.mrjimin.invest.domain.trading.service.TradingScheduler
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureTradingScheduler() {
    val scheduler by inject<TradingScheduler>()

    monitor.subscribe(ApplicationStarted) {
        scheduler.start()
    }

    monitor.subscribe(ApplicationStopped) {
        scheduler.stop()
    }
}