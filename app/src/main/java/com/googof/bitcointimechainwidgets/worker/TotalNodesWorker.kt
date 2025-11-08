package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.totalNodesPreference
import com.googof.bitcointimechainwidgets.network.BitnodesApi
import com.googof.bitcointimechainwidgets.widget.TotalNodesWidget

class TotalNodesWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        return try {
            val totalNodes = BitnodesApi.create().getSnapshots().results[0].total_nodes

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(TotalNodesWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                updateAppWidgetState(
                    applicationContext, it
                ) { prefs ->
                    prefs[totalNodesPreference] = totalNodes
                }

                TotalNodesWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("TotalNodesWorker", "Error updating widget", e)
            Result.retry()
        }
    }
}
