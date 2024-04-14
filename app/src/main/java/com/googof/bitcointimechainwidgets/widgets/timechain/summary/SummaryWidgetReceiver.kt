package com.googof.bitcointimechainwidgets.widgets.timechain.summary

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class SummaryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = SummaryWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        SummaryWidgetWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        SummaryWidgetWorker.cancel(context)
    }
}
