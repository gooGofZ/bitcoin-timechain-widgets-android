package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.totalNodesPreference
import com.googof.bitcointimechainwidgets.network.BitnodesApi
import com.googof.bitcointimechainwidgets.widget.TotalNodesWidget
import kotlin.reflect.KClass

class TotalNodesWorker(context: Context, workerParams: WorkerParameters) :
    BaseWidgetWorker(context, workerParams) {

    override val widgetClass: KClass<out GlanceAppWidget> = TotalNodesWidget::class
    override val tag = "TotalNodesWorker"

    override suspend fun fetchAndStore(context: Context, glanceId: GlanceId) {
        val totalNodes = BitnodesApi.instance.getSnapshots().results[0].total_nodes
        updateAppWidgetState(context, glanceId) { it[totalNodesPreference] = totalNodes }
    }
}
