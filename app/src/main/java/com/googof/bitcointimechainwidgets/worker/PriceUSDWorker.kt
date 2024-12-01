package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.WidgetStateDefinition
import com.googof.bitcointimechainwidgets.data.priceTimePreference
import com.googof.bitcointimechainwidgets.data.priceUsdPreference
import com.googof.bitcointimechainwidgets.network.MempoolApi
import com.googof.bitcointimechainwidgets.widget.PriceUSDWidget

// BitcoinPriceWorker.kt
class PriceUSDWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {

            val prices = MempoolApi.create().getPrices()

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(PriceUSDWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                val dataStore = WidgetStateDefinition.getDataStore(
                    applicationContext,
                    "price_usd_widget_prefs"
                )

                dataStore.updateData { prefs ->
                    prefs.toMutablePreferences().apply {
                        this[priceTimePreference] = prices.time
                        this[priceUsdPreference] = prices.USD
                    }
                }

                updateAppWidgetState(
                    context = applicationContext,
                    glanceId = it
                ) { prefs ->
                    prefs[priceTimePreference] = prices.time
                    prefs[priceUsdPreference] = prices.USD
                }

                PriceUSDWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            println(e)
            Result.retry()
        }
    }
}