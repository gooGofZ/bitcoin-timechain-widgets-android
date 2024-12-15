package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.nextHalvingDatePreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.DayUntilNextHalvingWidget

class DayUntilNextHalvingWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        return try {
            val nextHalvingEstimatedDate =
                BitcoinExplorerApi.create().getNextHalving().nextHalvingEstimatedDate

            Log.d("DayUntilNextHalvingWorker", nextHalvingEstimatedDate)

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(DayUntilNextHalvingWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                updateAppWidgetState(
                    applicationContext, it
                ) { prefs ->
                    prefs[nextHalvingDatePreferences] = nextHalvingEstimatedDate
                }

                DayUntilNextHalvingWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("DayUntilNextHalvingWorker", "Error updating widget", e)
            Result.retry()
        }
    }
}
