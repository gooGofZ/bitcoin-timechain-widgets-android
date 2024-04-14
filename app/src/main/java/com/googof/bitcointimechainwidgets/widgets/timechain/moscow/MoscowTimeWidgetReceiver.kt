package com.googof.bitcointimechainwidgets.widgets.timechain.moscow

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class MoscowTimeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = MoscowTimeWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        MoscowTimeWidgetWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        MoscowTimeWidgetWorker.cancel(context)
    }
}
