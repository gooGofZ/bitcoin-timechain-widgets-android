package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.priceThbPreference
import com.googof.bitcointimechainwidgets.network.CoinGeckoApi
import com.googof.bitcointimechainwidgets.widget.SiamTimeWidget
import kotlin.reflect.KClass

class SiamTimeWorker(context: Context, workerParams: WorkerParameters) :
    BaseWidgetWorker(context, workerParams) {

    override val widgetClass: KClass<out GlanceAppWidget> = SiamTimeWidget::class
    override val tag = "SiamTimeWorker"

    override suspend fun fetchAndStore(context: Context, glanceId: GlanceId) {
        val priceThb = CoinGeckoApi.instance.getTHBPrice().bitcoin.thb
        updateAppWidgetState(context, glanceId) { it[priceThbPreference] = priceThb }
    }
}
