package com.googof.bitcointimechainwidgets.data

import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

var BLOCKS_PER_HALVING = 210_000
val priceUsdPreference = doublePreferencesKey("price_usd")
val blockHeightPreference = intPreferencesKey("block_height")
val supplyPreferences = stringPreferencesKey("supply")
val blockUntilNextHalvingPreferences = intPreferencesKey("block_until_next_halving")
val nowPreferences = stringPreferencesKey("now")
val feeLowPreferences = intPreferencesKey("fee_low")
val feeMedPreferences = intPreferencesKey("fee_med")
val feeHighPreferences = intPreferencesKey("fee_high")
val marketCapPreferences = doublePreferencesKey("market_cap")
val halvingProgressPreferences = doublePreferencesKey("halving_progress")
val nextHalvingDatePreferences = stringPreferencesKey("next_halving_date")
val hashRatePreference = stringPreferencesKey("hashrate")
val totalNodesPreference = intPreferencesKey("total_nodes")
val quoteTextPreference = stringPreferencesKey("quote_text")
val quoteSpeakerPreferences = stringPreferencesKey("quote_speaker")
val quoteDatePreference = stringPreferencesKey("quote_date")

fun calculateHalvingProgress(blocksUntilNextHalving: Int): Double {
    // Calculate completed blocks
    val blocksCompleted = BLOCKS_PER_HALVING - blocksUntilNextHalving

    // Calculate percentage
    return (blocksCompleted.toDouble() / BLOCKS_PER_HALVING) * 100
}

fun convertToLocalDateTime(utcDateString: String): LocalDateTime {

    // Parse the UTC date string to ZonedDateTime
    val utcDateTime = ZonedDateTime.parse(utcDateString)

    // Convert to local timezone
    return utcDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
}

fun calculateDaysUntilHalving(utcDateString: String): Long {
    val now = LocalDateTime.now()
    val halvingDate = convertToLocalDateTime(utcDateString)
    return ChronoUnit.DAYS.between(now, halvingDate)
}
