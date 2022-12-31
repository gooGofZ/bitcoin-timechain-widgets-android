package com.googof.bitcointimechainwidgets.mempool.lightning

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.googof.bitcointimechainwidgets.mempool.LightningNetworkGlanceWidget

class LightningNetworkGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = LightningNetworkGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        LightningNetworkWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        LightningNetworkWorker.cancel(context)
    }
}
