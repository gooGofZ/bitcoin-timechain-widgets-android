package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.feeHighPreferences
import com.googof.bitcointimechainwidgets.data.feeLowPreferences
import com.googof.bitcointimechainwidgets.data.feeMedPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.FeePriorityWidget
import kotlin.reflect.KClass

class FeePriorityWorker(context: Context, workerParams: WorkerParameters) :
    BaseWidgetWorker(context, workerParams) {

    override val widgetClass: KClass<out GlanceAppWidget> = FeePriorityWidget::class
    override val tag = "FeePriorityWorker"

    override suspend fun fetchAndStore(context: Context, glanceId: GlanceId) {
        val fees = BitcoinExplorerApi.instance.getMempoolFees()
        updateAppWidgetState(context, glanceId) {
            it[feeLowPreferences] = fees.oneDay
            it[feeMedPreferences] = fees.sixtyMin
            it[feeHighPreferences] = fees.thirtyMin
        }
    }
}
