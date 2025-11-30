package com.example.kmmwebsocket.MessageApp.Utility

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
object DateTimeUtils{
    fun currentTimeMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }

    fun formatTime(timestamp: Long, pattern: String = "HH:mm"): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localDateTime.hour.toString().padStart(2, '0')}:${
            localDateTime.minute.toString().padStart(2, '0')
        }"
    }

    fun formatDate(timestamp: Long, pattern: String = "MMM dd, yyyy"): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${getMonthName(localDateTime.month)} ${localDateTime.dayOfMonth}, ${localDateTime.year}"
    }

    private fun getMonthName(month: Month): String {
        return when (month) {
            Month.JANUARY -> "Jan"
            Month.FEBRUARY -> "Feb"
            Month.MARCH -> "Mar"
            Month.APRIL -> "Apr"
            Month.MAY -> "May"
            Month.JUNE -> "Jun"
            Month.JULY -> "Jul"
            Month.AUGUST -> "Aug"
            Month.SEPTEMBER -> "Sep"
            Month.OCTOBER -> "Oct"
            Month.NOVEMBER -> "Nov"
            Month.DECEMBER -> "Dec"
        }
    }

    fun isToday(timestamp: Long): Boolean {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val messageTime = Instant.fromEpochMilliseconds(timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        return now.date == messageTime.date
    }

    fun isYesterday(timestamp: Long): Boolean {
        val yesterday = Clock.System.now().minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val messageTime = Instant.fromEpochMilliseconds(timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        return yesterday.date == messageTime.date
    }
}