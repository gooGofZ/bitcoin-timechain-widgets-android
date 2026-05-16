package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.blocksToNextHalvingPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.BlocksToNextHalvingWidget
import kotlin.reflect.KClass

class BlocksToNextHalvingWorker(context: Context, workerParams: WorkerParameters) :
    BaseWidgetWorker(context, workerParams) {

    override val widgetClass: KClass<out GlanceAppWidget> = BlocksToNextHalvingWidget::class
    override val tag = "BlocksToNextHalvingWorker"

    override suspend fun fetchAndStore(context: Context, glanceId: GlanceId) {
        val blocks = BitcoinExplorerApi.instance.getNextHalving().blocksUntilNextHalving
        updateAppWidgetState(context, glanceId) { it[blocksToNextHalvingPreferences] = blocks }
    }
}
