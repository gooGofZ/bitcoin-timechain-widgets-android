package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.FeePriorityWidget
import com.googof.bitcointimechainwidgets.worker.FeePriorityWorker
import kotlin.reflect.KClass

class FeePriorityWidgetReceiver : BaseWidgetReceiver<FeePriorityWorker>() {
    override val glanceAppWidget: GlanceAppWidget = FeePriorityWidget()
    override val workerClass: KClass<FeePriorityWorker> = FeePriorityWorker::class
    override val workName = "fee_priority_update"
}
