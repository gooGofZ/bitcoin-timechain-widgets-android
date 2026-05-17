package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.SupplyWidget
import com.googof.bitcointimechainwidgets.worker.SupplyWorker
import kotlin.reflect.KClass

class SupplyWidgetReceiver : BaseWidgetReceiver<SupplyWorker>() {
    override val glanceAppWidget: GlanceAppWidget = SupplyWidget()
    override val workerClass: KClass<SupplyWorker> = SupplyWorker::class
    override val workName = "supply_update"
}
