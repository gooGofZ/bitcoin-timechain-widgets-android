package com.googof.bitcointimechainwidgets.mempool.blocks

import android.content.Context
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.text.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.items
import androidx.glance.layout.*
import androidx.glance.unit.ColorProvider
import com.googof.bitcointimechainwidgets.AppWidgetBox
import com.googof.bitcointimechainwidgets.AppWidgetColumn
import com.googof.bitcointimechainwidgets.GlanceTheme
import com.googof.bitcointimechainwidgets.R
import com.googof.bitcointimechainwidgets.common.getBlockWeightUsageFloat
import com.googof.bitcointimechainwidgets.common.getTimeAgo
import com.googof.bitcointimechainwidgets.mempool.MempoolInfo
import com.googof.bitcointimechainwidgets.mempool.MempoolInfoStateDefinition
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
class BlocksGlanceWidget : GlanceAppWidget() {

    // Override the state definition to use our custom one using Kotlin serialization
    override val stateDefinition = MempoolInfoStateDefinition

    @Composable
    override fun Content() {
        val mempoolInfo = currentState<MempoolInfo>()
        GlanceTheme {
            when (mempoolInfo) {
                MempoolInfo.Loading -> {
                    AppWidgetBox(
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = ColorProvider(R.color.white))
                    }
                }
                is MempoolInfo.Available -> {
                    AppWidgetColumn(
                    ) {
                        BlocksCompose(mempoolInfo)
                    }
                }

                is MempoolInfo.Unavailable -> {
                    AppWidgetColumn(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Data not available")
                        Button("Refresh", actionRunCallback<UpdateBlocksAction>())
                    }
                }
            }
        }
    }
}


@Composable
fun BlocksCompose(latestBlocks: MempoolInfo.Available) {
    val blocks = (latestBlocks.blocks)
    Row(
        modifier = GlanceModifier.wrapContentHeight().wrapContentWidth().fillMaxWidth(),
    )
    {
        Text(
            text = "The past 15 mined blocks",
            modifier = GlanceModifier.defaultWeight(),
            style = TextStyle(
                color = GlanceTheme.colors.textColorPrimary,
            )
        )
        Text(
            text = "Updated: " + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss")),
            style = TextStyle(
                color = GlanceTheme.colors.textColorSecondary,
                fontSize = 10.sp,
                textAlign = TextAlign.End
            ),
            modifier = GlanceModifier.clickable(actionRunCallback<UpdateBlocksAction>())
        )
    }
    Row(
        modifier = GlanceModifier.wrapContentHeight().wrapContentWidth().fillMaxWidth(),
    )
    {
        Text(
            text = "Height",
            modifier = GlanceModifier.width(80.dp),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        )
        Text(
            text = "Mined",
            modifier = GlanceModifier.defaultWeight(),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        )
        Text(
            text = "Reward",
            modifier = GlanceModifier.width(70.dp),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        )
        Text(
            text = "Weight",
            modifier = GlanceModifier.width(50.dp),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        )
    }
    LazyColumn {
        items(blocks) { block ->
            Row(
                modifier = GlanceModifier.wrapContentHeight().wrapContentWidth().fillMaxWidth()
                    .clickable(actionRunCallback<UpdateBlocksAction>()),
            ) {
                //Height
                Text(
                    text = block.height,
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = GlanceModifier.width(80.dp)
                )
                //Mined
                Text(text = getTimeAgo(block.timestamp), modifier = GlanceModifier.defaultWeight())
                //Reward
                Text(
                    text = String.format(
                        "%.2f",
                        (block.extras.reward.toDouble() / 100000000.toDouble())
                    ) + " BTC",
                    modifier = GlanceModifier.width(70.dp)
                )
                //Size
                LinearProgressIndicator(
                    progress = getBlockWeightUsageFloat(block.weight),
                    modifier = GlanceModifier.padding(2.dp).width(50.dp)
                )
            }
        }
    }
}

class UpdateBlocksAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Force the worker to refresh
        BlocksWorker.enqueue(context = context, force = true)
    }
}
