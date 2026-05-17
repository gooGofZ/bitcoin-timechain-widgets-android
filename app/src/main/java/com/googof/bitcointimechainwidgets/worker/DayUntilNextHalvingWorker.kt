package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.nextHalvingDatePreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.DayUntilNextHalvingWidget
import kotlin.reflect.KClass

class DayUntilNextHalvingWorker(context: Context, workerParams: WorkerParameters) :
    BaseWidgetWorker(context, workerParams) {

    override val widgetClass: KClass<out GlanceAppWidget> = DayUntilNextHalvingWidget::class
    override val tag = "DayUntilNextHalvingWorker"

    override suspend fun fetchAndStore(context: Context, glanceId: GlanceId) {
        val date = BitcoinExplorerApi.instance.getNextHalving().nextHalvingEstimatedDate
        updateAppWidgetState(context, glanceId) { it[nextHalvingDatePreferences] = date }
    }
}
