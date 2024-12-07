package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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

            Log.d("MarketCapWorker", "$marketCap")

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(MarketCapWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                updateAppWidgetState(
                    applicationContext, it
                ) { prefs ->
                    prefs[marketCapPreferences] = marketCap
                }

                MarketCapWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("MarketCapWorker", "Error during doWork", e)
            Result.retry()
        }
    }
}
