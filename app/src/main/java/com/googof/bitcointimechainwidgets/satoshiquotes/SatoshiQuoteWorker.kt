package com.googof.bitcointimechainwidgets.satoshiquotes

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.*
import java.time.Duration

class SatoshiQuoteWorker(
    private val context: Context, workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {

        private val uniqueWorkName = SatoshiQuoteWorker::class.java.simpleName

        fun enqueue(context: Context, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<SatoshiQuoteWorker>(
                Duration.ofMinutes(15)
            )
            var workPolicy = ExistingPeriodicWorkPolicy.KEEP

            // Replace any enqueued work and expedite the request
            if (force) {
                workPolicy = ExistingPeriodicWorkPolicy.REPLACE
            }

            manager.enqueueUniquePeriodicWork(
                uniqueWorkName, workPolicy, requestBuilder.build()
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
        val glanceIds = manager.getGlanceIds(SatoshiQuoteGlanceWidget::class.java)
        return try {
            // Update state to indicate loading
            setWidgetState(glanceIds, SatoshiQuoteInfo.Loading)
            // Update state with new data
            setWidgetState(glanceIds, SatoshiQuoteRepo.getRandomSatoshiQuote())

            Result.success()
        } catch (e: Exception) {
            setWidgetState(glanceIds, SatoshiQuoteInfo.Unavailable(e.message.orEmpty()))
            if (runAttemptCount < 10) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun setWidgetState(glanceIds: List<GlanceId>, newState: SatoshiQuoteInfo) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context = context,
                definition = SatoshiQuoteStateDefinition,
                glanceId = glanceId,
                updateState = { newState })
        }
        SatoshiQuoteGlanceWidget().updateAll(context)
    }
}