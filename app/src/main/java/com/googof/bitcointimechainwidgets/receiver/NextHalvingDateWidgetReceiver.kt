package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.NextHalvingDateWidget
import com.googof.bitcointimechainwidgets.worker.NextHalvingDateWorker
import kotlin.reflect.KClass

class NextHalvingDateWidgetReceiver : BaseWidgetReceiver<NextHalvingDateWorker>() {
    override val glanceAppWidget: GlanceAppWidget = NextHalvingDateWidget()
    override val workerClass: KClass<NextHalvingDateWorker> = NextHalvingDateWorker::class
    override val workName = "next_halving"
}
