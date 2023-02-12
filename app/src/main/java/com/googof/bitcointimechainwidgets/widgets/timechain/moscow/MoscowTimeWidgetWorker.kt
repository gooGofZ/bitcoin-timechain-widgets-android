package com.googof.bitcointimechainwidgets.widgets.timechain.moscow

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
import com.googof.bitcointimechainwidgets.widgets.timechain.TimechainInfo
import com.googof.bitcointimechainwidgets.widgets.timechain.TimechainRepo
import com.googof.bitcointimechainwidgets.widgets.timechain.TimechainStateDefinition
import java.time.Duration


class MoscowTimeWidgetWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    companion object {
        private val uniqueWorkName = MoscowTimeWidgetWorker::class.java.simpleName
        fun enqueue(context: Context, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<MoscowTimeWidgetWorker>(
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
        val glanceIds = manager.getGlanceIds(MoscowTimeWidget::class.java)
        return try {
            // Update state to indicate loading
            setWidgetState(glanceIds, TimechainInfo.Loading)
            // Update state with new data
            setWidgetState(glanceIds, TimechainRepo.getTimechainInfo())
            Result.success()
        } catch (e: Exception) {
            setWidgetState(glanceIds, TimechainInfo.Unavailable(e.message.orEmpty()))
            if (runAttemptCount < 10) {
                // Exponential backoff strategy will avoid the request to repeat
                // too fast in case of failures.
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun setWidgetState(glanceIds: List<GlanceId>, newState: TimechainInfo) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = TimechainStateDefinition,
                glanceId = glanceId,
                updateState = { newState }
            )
        }
        MoscowTimeWidget().updateAll(context)
    }
}
