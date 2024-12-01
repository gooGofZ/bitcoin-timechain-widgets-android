package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.WidgetStateDefinition
import com.googof.bitcointimechainwidgets.data.blockHeightPreference
import com.googof.bitcointimechainwidgets.network.MempoolApi
import com.googof.bitcointimechainwidgets.widget.BlockHeightWidget

// BitcoinWidgetWorker.kt
class BlockHeightWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val blockHeight = MempoolApi.create().getBlockHeight()

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(BlockHeightWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                // Get the DataStore using BitcoinWidgetStateDefinition
                val dataStore = WidgetStateDefinition.getDataStore(
                    applicationContext,
                    "block_height_widget_prefs"
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
                BlockHeightWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
