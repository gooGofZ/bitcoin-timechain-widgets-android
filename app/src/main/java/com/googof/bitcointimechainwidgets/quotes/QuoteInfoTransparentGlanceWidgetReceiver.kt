package com.googof.bitcointimechainwidgets.quotes

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class QuoteInfoTransparentGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = QuoteInfoTransparentGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        QuoteInfoWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        QuoteInfoWorker.cancel(context)
    }
}
