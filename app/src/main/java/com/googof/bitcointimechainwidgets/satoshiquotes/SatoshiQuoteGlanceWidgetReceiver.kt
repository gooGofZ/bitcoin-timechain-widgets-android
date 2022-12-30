package com.googof.bitcointimechainwidgets.satoshiquotes

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class SatoshiQuoteGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = SatoshiQuoteGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        SatoshiQuoteWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        SatoshiQuoteWorker.cancel(context)
    }
}