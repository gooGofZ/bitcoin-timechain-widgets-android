package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.supplyPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.SupplyWidget
import kotlin.reflect.KClass

class SupplyWorker(context: Context, workerParams: WorkerParameters) :
    BaseWidgetWorker(context, workerParams) {

    override val widgetClass: KClass<out GlanceAppWidget> = SupplyWidget::class
    override val tag = "SupplyWorker"

    override suspend fun fetchAndStore(context: Context, glanceId: GlanceId) {
        val supply = BitcoinExplorerApi.instance.getSupply().supply
        updateAppWidgetState(context, glanceId) { it[supplyPreferences] = supply }
    }
}
