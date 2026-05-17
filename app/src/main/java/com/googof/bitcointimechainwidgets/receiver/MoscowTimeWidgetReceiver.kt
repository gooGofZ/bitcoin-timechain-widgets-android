package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.MoscowTimeWidget
import com.googof.bitcointimechainwidgets.worker.MoscowTimeWorker
import kotlin.reflect.KClass

class MoscowTimeWidgetReceiver : BaseWidgetReceiver<MoscowTimeWorker>() {
    override val glanceAppWidget: GlanceAppWidget = MoscowTimeWidget()
    override val workerClass: KClass<MoscowTimeWorker> = MoscowTimeWorker::class
    override val workName = "moscow_time_widget_update"
}
