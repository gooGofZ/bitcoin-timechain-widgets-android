package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.TotalNodesWidget
import com.googof.bitcointimechainwidgets.worker.TotalNodesWorker
import kotlin.reflect.KClass

class TotalNodesWidgetReceiver : BaseWidgetReceiver<TotalNodesWorker>() {
    override val glanceAppWidget: GlanceAppWidget = TotalNodesWidget()
    override val workerClass: KClass<TotalNodesWorker> = TotalNodesWorker::class
    override val workName = "total_nodes_update"
    override val intervalMinutes: Long = 30L
}
