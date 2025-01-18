package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.priceThbPreference
import com.googof.bitcointimechainwidgets.network.CoinGeckoApi
import com.googof.bitcointimechainwidgets.widget.MoscowTimeWidget
import com.googof.bitcointimechainwidgets.widget.SiamTimeWidget

class SiamTimeWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        return try {
            val priceThb = CoinGeckoApi.create().getTHBPrice().bitcoin.thb

            Log.d("SiamTimeWorker", "$priceThb")

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(SiamTimeWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                updateAppWidgetState(
                    applicationContext, it
                ) { prefs ->
                    prefs[priceThbPreference] = priceThb
                }

                SiamTimeWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("SiamTimeWorker", "Error during doWork", e)
            Result.retry()
        }
    }
}
