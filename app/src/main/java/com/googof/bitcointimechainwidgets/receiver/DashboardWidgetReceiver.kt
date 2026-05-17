package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.DashboardWidget
import com.googof.bitcointimechainwidgets.worker.DashboardWorker
import kotlin.reflect.KClass

class DashboardWidgetReceiver : BaseWidgetReceiver<DashboardWorker>() {
    override val glanceAppWidget: GlanceAppWidget = DashboardWidget()
    override val workerClass: KClass<DashboardWorker> = DashboardWorker::class
    override val workName = "dashboard_update"
}
