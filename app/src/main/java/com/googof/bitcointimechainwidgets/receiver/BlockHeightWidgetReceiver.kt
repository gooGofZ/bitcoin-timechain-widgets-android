package com.googof.bitcointimechainwidgets.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.widget.BlockHeightWidget
import com.googof.bitcointimechainwidgets.worker.BlockHeightWorker
import kotlin.reflect.KClass

class BlockHeightWidgetReceiver : BaseWidgetReceiver<BlockHeightWorker>() {
    override val glanceAppWidget: GlanceAppWidget = BlockHeightWidget()
    override val workerClass: KClass<BlockHeightWorker> = BlockHeightWorker::class
    override val workName = "block_height_update"
}
