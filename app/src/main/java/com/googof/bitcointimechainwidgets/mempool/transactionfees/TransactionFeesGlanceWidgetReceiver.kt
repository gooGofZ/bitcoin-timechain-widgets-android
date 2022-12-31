package com.googof.bitcointimechainwidgets.mempool.transactionfees

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.googof.bitcointimechainwidgets.mempool.TransactionFeesGlanceWidget

class TransactionFeesGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = TransactionFeesGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        TransactionFeesWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        TransactionFeesWorker.cancel(context)
    }
}
