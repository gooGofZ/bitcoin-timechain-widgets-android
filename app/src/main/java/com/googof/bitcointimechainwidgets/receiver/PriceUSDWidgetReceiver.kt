package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.PriceUSDWidget
import com.googof.bitcointimechainwidgets.worker.PriceUSDWorker
import kotlin.reflect.KClass

class PriceUSDWidgetReceiver : BaseWidgetReceiver<PriceUSDWorker>() {
    override val glanceAppWidget: GlanceAppWidget = PriceUSDWidget()
    override val workerClass: KClass<PriceUSDWorker> = PriceUSDWorker::class
    override val workName = "price_usd_widget_update"
}
