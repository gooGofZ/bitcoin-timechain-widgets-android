package com.googof.bitcointimechainwidgets.mempool

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.*
import java.time.Duration

class MempoolWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {

        private val uniqueWorkName = MempoolWorker::class.java.simpleName

        fun enqueue(context: Context, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<MempoolWorker>(
                Duration.ofMinutes(15)
            )
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

        /**
         * Cancel any ongoing worker
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
        }
    }

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(MempoolGlanceWidget::class.java)
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

    /**
     * Update the state of all widgets and then force update UI
     */
    private suspend fun setWidgetState(glanceIds: List<GlanceId>, newState: MempoolInfo) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = MempoolInfoStateDefinition,
                glanceId = glanceId,
                updateState = { newState }
            )
        }
        MempoolGlanceWidget().updateAll(context)
    }
}