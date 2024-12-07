package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.priceUsdPreference
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.PriceUSDWidget

class PriceUSDWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("PriceUSDWorker", "Worker running")
        return try {

            val prices = BitcoinExplorerApi.create().getPrice()

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(PriceUSDWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                updateAppWidgetState(
                    applicationContext, it
                ) { prefs ->
                    prefs[priceUsdPreference] = prices.usd.toDouble()
                }

                PriceUSDWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("PriceUSDWorker", "Error during doWork", e)
            Result.retry()
        }
    }
}
