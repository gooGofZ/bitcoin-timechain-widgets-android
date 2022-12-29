package com.googof.bitcointimechainwidgets.satoshiquotes

import kotlinx.serialization.Serializable

@Serializable
sealed interface SatoshiQuoteInfo {
    @Serializable
    object Loading : SatoshiQuoteInfo

    @Serializable
    data class Available(
        val text: String,
        val category: String,
        val medium: String,
        val date: String,
    ): SatoshiQuoteInfo

    @Serializable
    data class Unavailable(val message: String) : SatoshiQuoteInfo
}

data class SatoshiQuote(
    val text: String,
    val category: String,
    val medium: String,
    val date: String,
)
