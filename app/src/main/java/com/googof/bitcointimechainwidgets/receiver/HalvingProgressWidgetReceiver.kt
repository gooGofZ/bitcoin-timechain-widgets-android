package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.HalvingProgressWidget
import com.googof.bitcointimechainwidgets.worker.HalvingProgressWorker
import kotlin.reflect.KClass

class HalvingProgressWidgetReceiver : BaseWidgetReceiver<HalvingProgressWorker>() {
    override val glanceAppWidget: GlanceAppWidget = HalvingProgressWidget()
    override val workerClass: KClass<HalvingProgressWorker> = HalvingProgressWorker::class
    override val workName = "halving_progress"
}
