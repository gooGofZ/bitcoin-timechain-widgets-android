package com.googof.bitcointimechainwidgets.mempool.blocks

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class BlocksGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = BlocksGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }
}
