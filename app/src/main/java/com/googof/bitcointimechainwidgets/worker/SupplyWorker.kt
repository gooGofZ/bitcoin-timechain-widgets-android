package com.googof.bitcointimechainwidgets.worker

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.supplyPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.SupplyWidget

class SupplyWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @SuppressLint("DefaultLocale")
    override suspend fun doWork(): Result {
        Log.d("SupplyWorker", "Worker running")

        return try {
            val supply = BitcoinExplorerApi.create().getSupply().supply

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(SupplyWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                updateAppWidgetState(
                    applicationContext, it
                ) { prefs ->
                    prefs[supplyPreferences] = supply
                }

                SupplyWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("SupplyWorker", "Error during doWork", e)
            Result.retry()
        }
    }
}
