package com.googof.bitcointimechainwidgets.common

import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.math.absoluteValue

private const val BLOCKHALVING = 210000
private const val BLOCK_WEIGHT_SIZE = 4000000
private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS

private fun currentDate(): Date {
    val calendar = Calendar.getInstance()
    return calendar.time
}

fun getTimeAgo(timestamp: Long): String {
    var time = timestamp

    if (time < 1000000000000L) {
        time *= 1000
    }

    val now = currentDate().time

    if (time > now || time <= 0) {
        return "in the future"
    }

    val diff = now - time
    return when {
        diff < MINUTE_MILLIS -> "moments ago"
        diff < 2 * MINUTE_MILLIS -> "a minute ago"
        diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
        diff < 2 * HOUR_MILLIS -> "an hour ago"
        diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
        diff < 48 * HOUR_MILLIS -> "yesterday"
        else -> "${diff / DAY_MILLIS} days ago"
    }
}

fun ordinalOf(i: Int): String {
    val iAbs = i.absoluteValue // if you want negative ordinals, or just use i
    return "$i" + if (iAbs % 100 in 11..13) "th" else when (iAbs % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}

fun remainBlockToHalving(currentBlockHeight: Int): Int {
    return ((currentBlockHeight / (BLOCKHALVING) + 1) * BLOCKHALVING) - currentBlockHeight
}


fun estimateDateToHalving(currentBlockHeight: Int): LocalDateTime {
    return (LocalDateTime.now()).plusMinutes(10 * (remainBlockToHalving(currentBlockHeight)).toLong())
}

fun daysToHalving(currentBlockHeight: Int): String {
    val estimateDate = estimateDateToHalving(currentBlockHeight)
    val today = LocalDateTime.now()
    return ((Duration.between(today, estimateDate).toDays()).toInt()).toString()
}

fun nextBlockHalving(currentBlockHeight: Int): String {
    return ordinalOf((currentBlockHeight / BLOCKHALVING) + 1)
}

fun getBlockWeightUsageFloat(weight: Int): Float {
    return weight.toFloat() / BLOCK_WEIGHT_SIZE.toFloat()
}