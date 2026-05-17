package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.SiamTimeWidget
import com.googof.bitcointimechainwidgets.worker.SiamTimeWorker
import kotlin.reflect.KClass

class SiamTimeWidgetReceiver : BaseWidgetReceiver<SiamTimeWorker>() {
    override val glanceAppWidget: GlanceAppWidget = SiamTimeWidget()
    override val workerClass: KClass<SiamTimeWorker> = SiamTimeWorker::class
    override val workName = "siam_time_widget_update"
}
