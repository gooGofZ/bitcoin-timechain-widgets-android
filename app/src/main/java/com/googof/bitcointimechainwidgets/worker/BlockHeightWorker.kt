package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.blockHeightPreference
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.BlockHeightWidget
import kotlin.reflect.KClass

class BlockHeightWorker(context: Context, workerParams: WorkerParameters) :
    BaseWidgetWorker(context, workerParams) {

    override val widgetClass: KClass<out GlanceAppWidget> = BlockHeightWidget::class
    override val tag = "BlockHeightWorker"

    override suspend fun fetchAndStore(context: Context, glanceId: GlanceId) {
        val blockHeight = BitcoinExplorerApi.instance.getLatestBlock().height
        updateAppWidgetState(context, glanceId) { it[blockHeightPreference] = blockHeight }
    }
}
