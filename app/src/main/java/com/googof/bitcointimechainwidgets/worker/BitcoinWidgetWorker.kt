package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.BitcoinWidgetStateDefinition
import com.googof.bitcointimechainwidgets.network.MempoolApi
import com.googof.bitcointimechainwidgets.widget.BitcoinBlockHeightWidget
import com.googof.bitcointimechainwidgets.widget.blockHeightPreference

// BitcoinWidgetWorker.kt
class BitcoinWidgetWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val blockHeight = MempoolApi.create().getBlockHeight()

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(BitcoinBlockHeightWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                // Get the DataStore using BitcoinWidgetStateDefinition
                val dataStore = BitcoinWidgetStateDefinition.getDataStore(
                    applicationContext,
                    "bitcoin_widget_prefs"
                )

                // Update preferences in the DataStore
                dataStore.updateData { prefs ->
                    prefs.toMutablePreferences().apply {
                        this[blockHeightPreference] = blockHeight
                    }
                }

                // Update the widget state
                updateAppWidgetState(
                    context = applicationContext,
                    glanceId = it
                ) { prefs ->
                    prefs[blockHeightPreference] = blockHeight
                }

                // Refresh the widget UI
                BitcoinBlockHeightWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
