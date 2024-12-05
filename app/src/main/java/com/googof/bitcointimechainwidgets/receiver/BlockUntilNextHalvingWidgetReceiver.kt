package com.googof.bitcointimechainwidgets.receiver

import android.content.Context
import java.util.concurrent.TimeUnit
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.googof.bitcointimechainwidgets.widget.BlockUntilNextHalvingWidget
import com.googof.bitcointimechainwidgets.worker.BlockUntilNextHalvingWorker

// BlockUntilNextHalvingWidgetReceiver.kt
class BlockUntilNextHalvingWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BlockUntilNextHalvingWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        setupPeriodicUpdate(context)
    }

    private fun setupPeriodicUpdate(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<BlockUntilNextHalvingWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "block_until_next_halving_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }
}
