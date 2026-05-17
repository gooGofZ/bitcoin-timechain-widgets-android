package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.marketCapPreferences
import com.googof.bitcointimechainwidgets.network.CoinGeckoApi
import com.googof.bitcointimechainwidgets.widget.MarketCapWidget
import kotlin.reflect.KClass

class MarketCapWorker(context: Context, workerParams: WorkerParameters) :
    BaseWidgetWorker(context, workerParams) {

    override val widgetClass: KClass<out GlanceAppWidget> = MarketCapWidget::class
    override val tag = "MarketCapWorker"

    override suspend fun fetchAndStore(context: Context, glanceId: GlanceId) {
        val marketCap = CoinGeckoApi.instance.getUSDPriceWithMarketCap().bitcoin.usd_market_cap
        updateAppWidgetState(context, glanceId) { it[marketCapPreferences] = marketCap }
    }
}
