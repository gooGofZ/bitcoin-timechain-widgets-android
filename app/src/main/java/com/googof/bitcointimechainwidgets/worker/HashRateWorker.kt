package com.googof.bitcointimechainwidgets.worker

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.hashRatePreference
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.HashRateWidget

class HashRateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @SuppressLint("DefaultLocale")
    override suspend fun doWork(): Result {

        return try {
            val hashRate = BitcoinExplorerApi.create().getHashRate().oneDay
            val hashRateString =
                String.format("%.2f %s/s", hashRate.`val`, hashRate.unitAbbreviation)

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(HashRateWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                updateAppWidgetState(
                    applicationContext, it
                ) { prefs ->
                    prefs[hashRatePreference] = hashRateString
                }

                HashRateWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("HashRateWorker", "Error updating widget", e)
            Result.retry()
        }
    }
}
