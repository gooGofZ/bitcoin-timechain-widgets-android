package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.WidgetStateDefinition
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.FeePriorityWidget
import com.googof.bitcointimechainwidgets.data.feeHighPreferences
import com.googof.bitcointimechainwidgets.data.feeLowPreferences
import com.googof.bitcointimechainwidgets.data.feeMedPreferences

class FeePriorityWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val fees = BitcoinExplorerApi.create().getMempoolFees()

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(FeePriorityWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                // Get the DataStore using BitcoinWidgetStateDefinition
                val dataStore = WidgetStateDefinition.getDataStore(
                    applicationContext,
                    "fee_priority_widget_prefs"
                )

                // Update preferences in the DataStore
                dataStore.updateData { prefs ->
                    prefs.toMutablePreferences().apply {
                        this[feeLowPreferences] = fees.oneDay
                        this[feeMedPreferences] = fees.sixtyMin
                        this[feeHighPreferences] = fees.thirtyMin
                    }
                }

                // Update the widget state
                updateAppWidgetState(
                    context = applicationContext,
                    glanceId = it
                ) { prefs ->
                    prefs[feeLowPreferences] = fees.oneDay
                    prefs[feeMedPreferences] = fees.sixtyMin
                    prefs[feeHighPreferences] = fees.thirtyMin
                }

                // Refresh the widget UI
                FeePriorityWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("FeePriorityWorker", "Error during doWork", e)
            Result.retry()
        }
    }
}
