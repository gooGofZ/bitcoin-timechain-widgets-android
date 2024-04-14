package com.googof.bitcointimechainwidgets.widgets.timechain.halving

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class NextHalvingWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = NextHalvingWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        NextHalvingWidgetWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        NextHalvingWidgetWorker.cancel(context)
    }
}
