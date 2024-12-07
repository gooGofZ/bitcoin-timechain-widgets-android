package com.googof.bitcointimechainwidgets.receiver

import android.content.Context
import java.util.concurrent.TimeUnit
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.googof.bitcointimechainwidgets.widget.BlockHeightWidget
import com.googof.bitcointimechainwidgets.worker.BlockHeightWorker

// BitcoinWidgetReceiver.kt
class BlockHeightWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BlockHeightWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        setupPeriodicUpdate(context)
    }

    private fun setupPeriodicUpdate(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<BlockHeightWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "block_height_update",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }
}
