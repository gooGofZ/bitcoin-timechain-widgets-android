package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.WidgetStateDefinition
import com.googof.bitcointimechainwidgets.data.nowPreferences
import com.googof.bitcointimechainwidgets.widget.NowWidget
import java.time.LocalDateTime

// NowWorker.kt
class NowWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(NowWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                // Get the DataStore using BitcoinWidgetStateDefinition
                val dataStore = WidgetStateDefinition.getDataStore(
                    applicationContext,
                    "now_widget_prefs"
                )

                // Update preferences in the DataStore
                dataStore.updateData { prefs ->
                    prefs.toMutablePreferences().apply {
                        this[nowPreferences] = LocalDateTime.now().toString()
                    }
                }

                // Update the widget state
                updateAppWidgetState(
                    context = applicationContext,
                    glanceId = it
                ) { prefs ->
                    prefs[nowPreferences] = LocalDateTime.now().toString()
                }

                // Refresh the widget UI
                NowWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
