package com.googof.bitcointimechainwidgets.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile

// BitcoinWidgetStateDefinition.kt
object WidgetStateDefinition {
    fun getDataStore(context: Context, fileKey: String): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(fileKey) }
        )
    }
}

val priceTimePreference = longPreferencesKey("price_time")
val priceUsdPreference = intPreferencesKey("price_usd")
val blockHeightPreference = intPreferencesKey("block_height")