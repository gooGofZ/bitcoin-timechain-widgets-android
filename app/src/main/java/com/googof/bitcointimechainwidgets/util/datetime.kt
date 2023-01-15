package com.googof.bitcointimechainwidgets.util

import android.text.format.DateUtils
import java.util.*

fun getRelationTime(time: Long): String {
    val now: Long = Date().time
    val delta = now - time
    val resolution: Long = if (delta <= DateUtils.MINUTE_IN_MILLIS) {
        DateUtils.SECOND_IN_MILLIS
    } else if (delta <= DateUtils.HOUR_IN_MILLIS) {
        DateUtils.MINUTE_IN_MILLIS
    } else if (delta <= DateUtils.DAY_IN_MILLIS) {
        DateUtils.HOUR_IN_MILLIS
    } else if (delta <= DateUtils.WEEK_IN_MILLIS) {
        DateUtils.DAY_IN_MILLIS
    } else {
        return (delta / DateUtils.WEEK_IN_MILLIS).toInt().toString() + " weeks(s) ago"
    }
    return DateUtils.getRelativeTimeSpanString(time, now, resolution).toString()
}