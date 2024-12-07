package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.blockUntilNextHalvingPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.BlockUntilNextHalvingWidget

class BlockUntilNextHalvingWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        return try {
            val blocksUntilNextHalving =
                BitcoinExplorerApi.create().getNextHalving().blocksUntilNextHalving

            Log.d("BlockUntilNextHalvingWorker", "$blocksUntilNextHalving")

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(BlockUntilNextHalvingWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                updateAppWidgetState(
                    applicationContext, it
                ) { prefs ->
                    prefs[blockUntilNextHalvingPreferences] = blocksUntilNextHalving
                }

                BlockUntilNextHalvingWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("BlockUntilNextHalvingWorker", "Error updating widget", e)
            Result.retry()
        }
    }
}
