package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.DayUntilNextHalvingWidget
import com.googof.bitcointimechainwidgets.worker.DayUntilNextHalvingWorker
import kotlin.reflect.KClass

class DayUntilNextHalvingWidgetReceiver : BaseWidgetReceiver<DayUntilNextHalvingWorker>() {
    override val glanceAppWidget: GlanceAppWidget = DayUntilNextHalvingWidget()
    override val workerClass: KClass<DayUntilNextHalvingWorker> = DayUntilNextHalvingWorker::class
    override val workName = "day_until_next_halving"
}
