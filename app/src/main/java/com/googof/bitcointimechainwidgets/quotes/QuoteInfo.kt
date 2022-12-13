package com.googof.bitcointimechainwidgets.quotes

import kotlinx.serialization.Serializable

@Serializable
sealed interface QuoteInfo {
    @Serializable
    object Loading: QuoteInfo

    @Serializable
    data class Available(
        val text: String,
        val speaker: String,
        val date: String,
    ) : QuoteInfo

    @Serializable
    data class Unavailable(val message: String) : QuoteInfo

}

//BitcoinExplorer.io
data class Quote(
    val text: String,
    val speaker: String,
    val date: String,
)