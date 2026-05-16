package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.priceUsdPreference
import com.googof.bitcointimechainwidgets.network.CoinGeckoApi
import com.googof.bitcointimechainwidgets.widget.MoscowTimeWidget
import kotlin.reflect.KClass

class MoscowTimeWorker(context: Context, workerParams: WorkerParameters) :
    BaseWidgetWorker(context, workerParams) {

    override val widgetClass: KClass<out GlanceAppWidget> = MoscowTimeWidget::class
    override val tag = "MoscowTimeWorker"

    override suspend fun fetchAndStore(context: Context, glanceId: GlanceId) {
        val price = CoinGeckoApi.instance.getUSDPrice().bitcoin.usd
        updateAppWidgetState(context, glanceId) { it[priceUsdPreference] = price }
    }
}
