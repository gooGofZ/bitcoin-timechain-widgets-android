package com.googof.bitcointimechainwidgets.mempool.blockheight

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.googof.bitcointimechainwidgets.mempool.BlockHeightGlanceWidget

class BlockHeightGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = BlockHeightGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        BlockHeightWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        BlockHeightWorker.cancel(context)
    }
}
