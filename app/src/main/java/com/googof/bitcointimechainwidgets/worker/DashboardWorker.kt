package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.repository.BitcoinDataRepository
import com.googof.bitcointimechainwidgets.widget.DashboardWidget

class DashboardWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("DashboardWorker", "Starting dashboard data refresh")
            
            val repository = BitcoinDataRepository(applicationContext)
            repository.refreshAllData()

            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(DashboardWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                DashboardWidget().update(applicationContext, it)
            }

            Log.d("DashboardWorker", "Dashboard data refresh completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e("DashboardWorker", "Error updating dashboard widget", e)
            Result.retry()
        }
    }
}