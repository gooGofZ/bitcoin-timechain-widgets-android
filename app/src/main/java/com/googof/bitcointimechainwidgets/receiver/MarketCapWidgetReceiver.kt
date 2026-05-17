package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.MarketCapWidget
import com.googof.bitcointimechainwidgets.worker.MarketCapWorker
import kotlin.reflect.KClass

class MarketCapWidgetReceiver : BaseWidgetReceiver<MarketCapWorker>() {
    override val glanceAppWidget: GlanceAppWidget = MarketCapWidget()
    override val workerClass: KClass<MarketCapWorker> = MarketCapWorker::class
    override val workName = "market_cap_update"
}
