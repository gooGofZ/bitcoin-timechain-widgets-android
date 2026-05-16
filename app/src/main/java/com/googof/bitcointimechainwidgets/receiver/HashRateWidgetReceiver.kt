package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.HashRateWidget
import com.googof.bitcointimechainwidgets.worker.HashRateWorker
import kotlin.reflect.KClass

class HashRateWidgetReceiver : BaseWidgetReceiver<HashRateWorker>() {
    override val glanceAppWidget: GlanceAppWidget = HashRateWidget()
    override val workerClass: KClass<HashRateWorker> = HashRateWorker::class
    override val workName = "hashrate_update"
}
