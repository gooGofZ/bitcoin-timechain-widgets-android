package com.googof.bitcointimechainwidgets.util

import java.text.SimpleDateFormat
import java.util.Locale

private val ISO_INPUT_FORMATS = listOf(
    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
    "yyyy-MM-dd'T'HH:mm:ss'Z'",
    "yyyy-MM-dd",
    "dd/MM/yyyy",
    "MM/dd/yyyy"
)

fun formatIsoDate(dateString: String, outputPattern: String): String {
    if (dateString.isEmpty()) return ""
    for (format in ISO_INPUT_FORMATS) {
        try {
            val sdf = SimpleDateFormat(format, Locale.US).apply { isLenient = false }
            val date = sdf.parse(dateString) ?: continue
            return SimpleDateFormat(outputPattern, Locale.US).format(date)
        } catch (_: Exception) {
            continue
        }
    }
    return dateString
}
