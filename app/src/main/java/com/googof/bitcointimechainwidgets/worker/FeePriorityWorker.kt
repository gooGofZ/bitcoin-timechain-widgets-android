package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
        Log.d("FeePriorityWorker", "Worker running")

        return try {
            val fees = BitcoinExplorerApi.create().getMempoolFees()

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(FeePriorityWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                updateAppWidgetState(
                    applicationContext, it
                ) { prefs ->
                    prefs[feeLowPreferences] = fees.oneDay
                    prefs[feeMedPreferences] = fees.sixtyMin
                    prefs[feeHighPreferences] = fees.thirtyMin
                }

                FeePriorityWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("FeePriorityWorker", "Error during doWork", e)
            Result.retry()
        }
    }
}
