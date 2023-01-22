package com.googof.bitcointimechainwidgets.widgets.mempool.blockheight

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class BlockHeightWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = BlockHeightWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        BlockHeightWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        BlockHeightWorker.cancel(context)
    }
}
