package com.googof.bitcointimechainwidgets.receiver

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

abstract class BaseWidgetReceiver<W : CoroutineWorker> : GlanceAppWidgetReceiver() {

    abstract val workerClass: KClass<W>
    abstract val workName: String
    open val intervalMinutes: Long = 15L
    open val flexMinutes: Long = 5L

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        setupPeriodicUpdate(context)
        WorkManager.getInstance(context).enqueueUniqueWork(
            "${workName}_initial",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequest.Builder(workerClass.java).build()
        )
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WorkManager.getInstance(context).cancelUniqueWork(workName)
    }

    private fun setupPeriodicUpdate(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequest.Builder(
            workerClass.java, intervalMinutes, TimeUnit.MINUTES, flexMinutes, TimeUnit.MINUTES
        ).setConstraints(constraints).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            workName,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}
