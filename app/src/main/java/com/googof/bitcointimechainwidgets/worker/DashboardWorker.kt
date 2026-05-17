package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.repository.BitcoinDataRepository
import com.googof.bitcointimechainwidgets.widget.DashboardWidget
import kotlin.reflect.KClass

class DashboardWorker(context: Context, workerParams: WorkerParameters) :
    BaseWidgetWorker(context, workerParams) {

    override val widgetClass: KClass<out GlanceAppWidget> = DashboardWidget::class
    override val tag = "DashboardWorker"

    override suspend fun fetchAndStore(context: Context, glanceId: GlanceId) {
        BitcoinDataRepository(context).refreshAllData()
    }
}
