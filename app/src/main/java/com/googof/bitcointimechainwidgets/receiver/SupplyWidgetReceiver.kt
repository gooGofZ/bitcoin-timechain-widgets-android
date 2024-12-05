package com.googof.bitcointimechainwidgets.receiver

import android.content.Context
import java.util.concurrent.TimeUnit
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.googof.bitcointimechainwidgets.widget.SupplyWidget
import com.googof.bitcointimechainwidgets.worker.SupplyWorker

// SupplyWidgetReceiver.kt
class SupplyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SupplyWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        setupPeriodicUpdate(context)
    }

    private fun setupPeriodicUpdate(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<SupplyWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "supply_update",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }
}
