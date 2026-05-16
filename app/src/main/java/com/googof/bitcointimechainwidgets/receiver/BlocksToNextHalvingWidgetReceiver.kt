package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.BlocksToNextHalvingWidget
import com.googof.bitcointimechainwidgets.worker.BlocksToNextHalvingWorker
import kotlin.reflect.KClass

class BlocksToNextHalvingWidgetReceiver : BaseWidgetReceiver<BlocksToNextHalvingWorker>() {
    override val glanceAppWidget: GlanceAppWidget = BlocksToNextHalvingWidget()
    override val workerClass: KClass<BlocksToNextHalvingWorker> = BlocksToNextHalvingWorker::class
    override val workName = "block_until_next_halving_work"
}
