package io.github.mrjimin.invest.domain.trading.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.milliseconds

class TradingScheduler(
    private val orderService: OrderService
) {

    private val logger =
        LoggerFactory.getLogger(TradingScheduler::class.java)

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    private var schedulerJob: Job? = null

    @Synchronized
    fun start() {
        if (schedulerJob?.isActive == true) return

        logger.info("TradingScheduler started")

        schedulerJob = scope.launch {
            while (isActive) {
                try {
                    val executedCount =
                        orderService.executePendingOrders()

                    if (executedCount > 0) {
                        logger.info(
                            "Pending orders executed: {}",
                            executedCount
                        )
                    }
                } catch (e: Exception) {
                    logger.error(
                        "TradingScheduler execution failed",
                        e
                    )
                }

                delay(10_000L.milliseconds)
            }
        }
    }

    @Synchronized
    fun stop() {
        schedulerJob?.cancel()
        schedulerJob = null

        logger.info("TradingScheduler stopped")
    }

    fun shutdown() {
        stop()
        scope.cancel()
    }
}
