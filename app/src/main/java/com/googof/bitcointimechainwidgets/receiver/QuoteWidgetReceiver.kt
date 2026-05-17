package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.QuoteWidget
import com.googof.bitcointimechainwidgets.worker.QuoteWorker
import kotlin.reflect.KClass

class QuoteWidgetReceiver : BaseWidgetReceiver<QuoteWorker>() {
    override val glanceAppWidget: GlanceAppWidget = QuoteWidget()
    override val workerClass: KClass<QuoteWorker> = QuoteWorker::class
    override val workName = "quote_update"
}
