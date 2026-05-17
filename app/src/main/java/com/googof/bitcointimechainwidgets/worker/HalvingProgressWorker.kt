package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.calculateHalvingProgress
import com.googof.bitcointimechainwidgets.data.halvingProgressPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.HalvingProgressWidget
import kotlin.reflect.KClass

class HalvingProgressWorker(context: Context, workerParams: WorkerParameters) :
    BaseWidgetWorker(context, workerParams) {

    override val widgetClass: KClass<out GlanceAppWidget> = HalvingProgressWidget::class
    override val tag = "HalvingProgressWorker"

    override suspend fun fetchAndStore(context: Context, glanceId: GlanceId) {
        val blocksUntilNextHalving = BitcoinExplorerApi.instance.getNextHalving().blocksUntilNextHalving
        updateAppWidgetState(context, glanceId) {
            it[halvingProgressPreferences] = calculateHalvingProgress(blocksUntilNextHalving)
        }
    }
}
