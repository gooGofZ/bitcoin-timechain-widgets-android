package com.googof.bitcointimechainwidgets.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

// BitcoinWidgetStateDefinition.kt
object BitcoinWidgetStateDefinition {
    fun getDataStore(context: Context, fileKey: String): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(fileKey) }
        )
    }
}
