package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.quoteTextPreference
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.QuoteWidget
import kotlin.reflect.KClass

class QuoteWorker(context: Context, workerParams: WorkerParameters) :
    BaseWidgetWorker(context, workerParams) {

    override val widgetClass: KClass<out GlanceAppWidget> = QuoteWidget::class
    override val tag = "QuoteWorker"

    override suspend fun fetchAndStore(context: Context, glanceId: GlanceId) {
        val quote = BitcoinExplorerApi.instance.getQuote()
        updateAppWidgetState(context, glanceId) {
            it[quoteTextPreference] = quote.quote
        }
    }
}
