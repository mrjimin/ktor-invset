package io.github.mrjimin.invest.common.time

import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime

class TradingCalendar(
    private val timeProvider: TimeProvider
) {
    fun isTradingDay(dateTime: ZonedDateTime = timeProvider.nowKst()): Boolean =
        dateTime.dayOfWeek != DayOfWeek.SATURDAY && dateTime.dayOfWeek != DayOfWeek.SUNDAY

    fun isMarketOpen(dateTime: ZonedDateTime = timeProvider.nowKst()): Boolean {
        if (!isTradingDay(dateTime)) return false
        val time = dateTime.toLocalTime()
        return !time.isBefore(MARKET_OPEN) && time.isBefore(MARKET_CLOSE)
    }

    companion object {
        val MARKET_OPEN: LocalTime = LocalTime.of(7, 30)
        val MARKET_CLOSE: LocalTime = LocalTime.of(17, 0)
    }
}