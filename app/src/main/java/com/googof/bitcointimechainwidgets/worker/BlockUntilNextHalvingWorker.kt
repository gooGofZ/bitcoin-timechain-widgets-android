package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.WidgetStateDefinition
import com.googof.bitcointimechainwidgets.data.blockUntilNextHalvingPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.BlockUntilNextHalvingWidget

// BlockUntilNextHalvingWorker.kt
class BlockUntilNextHalvingWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val blocksUntilNextHalving =
                BitcoinExplorerApi.create().getNextHalving().blocksUntilNextHalving

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(BlockUntilNextHalvingWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                // Get the DataStore using BitcoinWidgetStateDefinition
                val dataStore = WidgetStateDefinition.getDataStore(
                    applicationContext,
                    "block_until_next_halving_prefs"
                )

                // Update preferences in the DataStore
                dataStore.updateData { prefs ->
                    prefs.toMutablePreferences().apply {
                        this[blockUntilNextHalvingPreferences] = blocksUntilNextHalving
                    }
                }

                // Update the widget state
                updateAppWidgetState(
                    context = applicationContext,
                    glanceId = it
                ) { prefs ->
                    prefs[blockUntilNextHalvingPreferences] = blocksUntilNextHalving
                }

                // Refresh the widget UI
                BlockUntilNextHalvingWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
