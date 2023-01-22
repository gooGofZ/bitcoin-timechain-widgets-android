package com.googof.bitcointimechainwidgets.mempool.halving

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class NextHalvingGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = NextHalvingGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        NextHalvingWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        NextHalvingWorker.cancel(context)
    }
}