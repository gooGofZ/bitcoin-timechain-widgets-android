package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.calculateHalvingProgress
import com.googof.bitcointimechainwidgets.data.halvingProgressPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.HalvingProgressWidget

class HalvingProgressWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        return try {
            val blocksUntilNextHalving =
                BitcoinExplorerApi.create().getNextHalving().blocksUntilNextHalving

            Log.d("HalvingProgressWorker", "$blocksUntilNextHalving")

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(HalvingProgressWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                updateAppWidgetState(
                    applicationContext, it
                ) { prefs ->
                    prefs[halvingProgressPreferences] =
                        calculateHalvingProgress(blocksUntilNextHalving)
                }

                HalvingProgressWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("HalvingProgressWorker", "Error updating widget", e)
            Result.retry()
        }
    }
}
