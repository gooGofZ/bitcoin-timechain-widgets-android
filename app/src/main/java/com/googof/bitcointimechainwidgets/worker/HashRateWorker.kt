package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.hashRatePreference
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.HashRateWidget
import kotlin.reflect.KClass

class HashRateWorker(context: Context, workerParams: WorkerParameters) :
    BaseWidgetWorker(context, workerParams) {

    override val widgetClass: KClass<out GlanceAppWidget> = HashRateWidget::class
    override val tag = "HashRateWorker"

    override suspend fun fetchAndStore(context: Context, glanceId: GlanceId) {
        val hashRate = BitcoinExplorerApi.instance.getHashRate().oneDay
        val formatted = String.format("%.2f %s/s", hashRate.`val`, hashRate.unitAbbreviation)
        updateAppWidgetState(context, glanceId) { it[hashRatePreference] = formatted }
    }
}
