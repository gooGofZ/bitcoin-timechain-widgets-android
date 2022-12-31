package com.googof.bitcointimechainwidgets.mempool

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.layout.*
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.googof.bitcointimechainwidgets.AppWidgetBox
import com.googof.bitcointimechainwidgets.AppWidgetColumn
import com.googof.bitcointimechainwidgets.GlanceTheme
import com.googof.bitcointimechainwidgets.R
import com.googof.bitcointimechainwidgets.mempool.blockheight.BlockHeightWorker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BlockHeightGlanceWidget : GlanceAppWidget() {

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
                        modifier = GlanceModifier.appWidgetBackground()
                            .background(R.color.widget_background_color)
                    ) {
                        CircularProgressIndicator(color = ColorProvider(R.color.white))
                    }
                }
                is MempoolInfo.Available -> {
                    AppWidgetColumn(
                        modifier = GlanceModifier.appWidgetBackground()
                            .background(R.color.widget_background_color)
                    ) {
                        BlockHeightCompose(mempoolInfo)
                    }
                }

                is MempoolInfo.Unavailable -> {
                    AppWidgetColumn(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Data not available")
                        Button("Refresh", actionRunCallback<UpdateBlockHeightAction>())
                    }
                }
            }
        }
    }
}


@Composable
fun BlockHeightCompose(mempoolInfo: MempoolInfo.Available) {
    Column(
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = GlanceModifier.fillMaxSize()
            .clickable(actionRunCallback<UpdateBlockHeightAction>()),
    ) {

        Text(
            text = "Block Height",
            style = TextStyle(
                color = ColorProvider(R.color.widget_text_color),
            ),
            modifier = GlanceModifier.clickable(actionRunCallback<UpdateBlockHeightAction>())
        )

        Text(
            text = mempoolInfo.blockHeight,
            style = TextStyle(
                color = ColorProvider(R.color.widget_text_color),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            ),
            modifier = GlanceModifier.clickable(actionRunCallback<UpdateBlockHeightAction>())
        )
    }
}

/**
 * Force update the info after user click
 */
class UpdateBlockHeightAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Force the worker to refresh
        BlockHeightWorker.enqueue(context = context, force = true)
    }
}
