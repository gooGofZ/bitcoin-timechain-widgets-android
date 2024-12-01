package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.WidgetStateDefinition
import com.googof.bitcointimechainwidgets.data.priceUsdPreference
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.MoscowTimeWidget

// MoscowTimeWidget.kt
class MoscowTimeWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {

            val prices = BitcoinExplorerApi.create().getPrice()

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(MoscowTimeWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                val dataStore = WidgetStateDefinition.getDataStore(
                    applicationContext,
                    "moscow_time_widget_prefs"
                )

                dataStore.updateData { prefs ->
                    prefs.toMutablePreferences().apply {
                        this[priceUsdPreference] = prices.usd.toInt()
                    }
                }

                updateAppWidgetState(
                    context = applicationContext,
                    glanceId = it
                ) { prefs ->
                    prefs[priceUsdPreference] = prices.usd.toInt()
                }

                MoscowTimeWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}