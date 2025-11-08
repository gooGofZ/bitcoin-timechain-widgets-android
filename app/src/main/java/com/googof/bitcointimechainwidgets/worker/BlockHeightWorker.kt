package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.blockHeightPreference
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.BlockHeightWidget

class BlockHeightWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        return try {
            val blockHeight = BitcoinExplorerApi.create().getLatestBlock().height

            Log.d("BlockHeightWorker", "$blockHeight")

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(BlockHeightWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                updateAppWidgetState(
                    applicationContext, it
                ) { prefs ->
                    prefs[blockHeightPreference] = blockHeight
                }

                BlockHeightWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("BlockHeightWorker", "Error updating widget", e)
            Result.retry()
        }
    }
}
