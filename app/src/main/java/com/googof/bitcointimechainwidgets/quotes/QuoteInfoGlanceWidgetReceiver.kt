package com.googof.bitcointimechainwidgets.quotes

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class QuoteInfoGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = QuoteInfoGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        QuoteInfoWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        QuoteInfoWorker.cancel(context)
    }
}
