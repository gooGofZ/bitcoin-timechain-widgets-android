package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.WidgetStateDefinition
import com.googof.bitcointimechainwidgets.data.marketCapPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.MarketCapWidget

class MarketCapWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val marketCap = BitcoinExplorerApi.create().getMarketCap().usd

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(MarketCapWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                // Get the DataStore using BitcoinWidgetStateDefinition
                val dataStore = WidgetStateDefinition.getDataStore(
                    applicationContext,
                    "market_cap_widget_prefs"
                )


                // Update preferences in the DataStore
                dataStore.updateData { prefs ->
                    prefs.toMutablePreferences().apply {
                        this[marketCapPreferences] = marketCap
                    }
                }

                // Update the widget state
                updateAppWidgetState(
                    context = applicationContext,
                    glanceId = it
                ) { prefs ->
                    prefs[marketCapPreferences] = marketCap
                }

                // Refresh the widget UI
                MarketCapWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
