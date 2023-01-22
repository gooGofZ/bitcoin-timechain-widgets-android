package com.googof.bitcointimechainwidgets.mempool.halving

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.mempool.MempoolInfo
import com.googof.bitcointimechainwidgets.mempool.MempoolInfoStateDefinition
import com.googof.bitcointimechainwidgets.mempool.MempoolRepo
import java.time.Duration

class NextHalvingWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    companion object {
        private val uniqueWorkName = NextHalvingWorker::class.java.simpleName

        fun enqueue(context: Context, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder =
                PeriodicWorkRequestBuilder<NextHalvingWorker>(Duration.ofMinutes(15))

            var workPolicy = ExistingPeriodicWorkPolicy.KEEP

            // Replace any enqueued work and expedite the request
            if (force) {
                workPolicy = ExistingPeriodicWorkPolicy.REPLACE
            }

            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                workPolicy,
                requestBuilder.build()
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
        }
    }

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(NextHalvingGlanceWidget::class.java)
        return try {
            // Update state to indicate loading
            setWidgetState(glanceIds, MempoolInfo.Loading)
            // Update state with new data
            setWidgetState(glanceIds, MempoolRepo.getMempoolInfo())

            Result.success()
        } catch (e: Exception) {
            setWidgetState(glanceIds, MempoolInfo.Unavailable(e.message.orEmpty()))
            if (runAttemptCount < 10) {
                // Exponential backoff strategy will avoid the request to repeat
                // too fast in case of failures.
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun setWidgetState(glanceIds: List<GlanceId>, newState: MempoolInfo) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = MempoolInfoStateDefinition,
                glanceId = glanceId,
                updateState = { newState }
            )
        }
        NextHalvingGlanceWidget().updateAll(context)
    }
}
