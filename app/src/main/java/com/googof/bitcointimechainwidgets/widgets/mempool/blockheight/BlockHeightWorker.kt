package com.googof.bitcointimechainwidgets.widgets.mempool.blockheight

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
import com.googof.bitcointimechainwidgets.widgets.mempool.Mempool
import com.googof.bitcointimechainwidgets.widgets.mempool.MempoolRepo
import com.googof.bitcointimechainwidgets.widgets.mempool.MempoolStateDefinition
import java.time.Duration


class BlockHeightWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {
        private val uniqueWorkName = BlockHeightWorker::class.java.simpleName
        fun enqueue(context: Context, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<BlockHeightWorker>(
                Duration.ofMinutes(15)
            )
            var workPolicy = ExistingPeriodicWorkPolicy.KEEP

            // Replace any enqueued work and expedite the request
            if (force) {
                workPolicy = ExistingPeriodicWorkPolicy.UPDATE
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
        val glanceIds = manager.getGlanceIds(BlockHeightWidget::class.java)
        return try {
            // Update state to indicate loading
            setWidgetState(glanceIds, Mempool.Loading)
            // Update state with new data
            setWidgetState(glanceIds, MempoolRepo.getMempoolInfo())

            Result.success()
        } catch (e: Exception) {
            setWidgetState(glanceIds, Mempool.Unavailable(e.message.orEmpty()))
            if (runAttemptCount < 10) {
                // Exponential backoff strategy will avoid the request to repeat
                // too fast in case of failures.
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun setWidgetState(glanceIds: List<GlanceId>, newState: Mempool) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = MempoolStateDefinition,
                glanceId = glanceId,
                updateState = { newState }
            )
        }
        BlockHeightWidget().updateAll(context)
    }
}
