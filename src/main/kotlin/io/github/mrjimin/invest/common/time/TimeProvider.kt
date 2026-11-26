package io.github.mrjimin.invest.common.time

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Clock
import kotlin.time.Instant

interface TimeProvider {
    fun nowKst(): ZonedDateTime
    fun nowUtc(): LocalDateTime
    fun setFixedTime(kstTime: ZonedDateTime?)
}

class AdjustableTimeProvider : TimeProvider {
    @Volatile
    private var fixedKstTime: ZonedDateTime? = null

    override fun nowKst(): ZonedDateTime =
        fixedKstTime ?: ZonedDateTime.now(KST_ZONE)

    override fun nowUtc(): LocalDateTime {
        val fixed = fixedKstTime
        val instant = if (fixed != null) {
            Instant.fromEpochMilliseconds(fixed.toInstant().toEpochMilli())
        } else {
            Clock.System.now()
        }
        return instant.toLocalDateTime(TimeZone.UTC)
    }

    override fun setFixedTime(kstTime: ZonedDateTime?) {
        fixedKstTime = kstTime?.withZoneSameInstant(KST_ZONE)
    }

    companion object {
        val KST_ZONE: ZoneId = ZoneId.of("Asia/Seoul")
    }
}