package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.blocksToNextHalvingPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.BlocksToNextHalvingWidget

class BlocksToNextHalvingWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        return try {
            val blocksUntilNextHalving =
                BitcoinExplorerApi.create().getNextHalving().blocksUntilNextHalving

            Log.d("BlockUntilNextHalvingWorker", "$blocksUntilNextHalving")

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(BlocksToNextHalvingWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                updateAppWidgetState(
                    applicationContext, it
                ) { prefs ->
                    prefs[blocksToNextHalvingPreferences] = blocksUntilNextHalving
                }

                BlocksToNextHalvingWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("BlockUntilNextHalvingWorker", "Error updating widget", e)
            Result.retry()
        }
    }
}
