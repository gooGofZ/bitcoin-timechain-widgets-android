package com.googof.bitcointimechainwidgets.worker

import android.annotation.SuppressLint
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.WidgetStateDefinition
import com.googof.bitcointimechainwidgets.data.supplyPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.SupplyWidget

// SupplyWorker.kt
class SupplyWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @SuppressLint("DefaultLocale")
    override suspend fun doWork(): Result {
        return try {
            val supply = BitcoinExplorerApi.create().getSupply().supply


            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(SupplyWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                // Get the DataStore using BitcoinWidgetStateDefinition
                val dataStore = WidgetStateDefinition.getDataStore(
                    applicationContext,
                    "supply_widget_prefs"
                )

                // Update preferences in the DataStore
                dataStore.updateData { prefs ->
                    prefs.toMutablePreferences().apply {
                        this[supplyPreferences] = supply
                    }
                }

                // Update the widget state
                updateAppWidgetState(
                    context = applicationContext,
                    glanceId = it
                ) { prefs ->
                    prefs[supplyPreferences] = supply
                }

                // Refresh the widget UI
                SupplyWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
