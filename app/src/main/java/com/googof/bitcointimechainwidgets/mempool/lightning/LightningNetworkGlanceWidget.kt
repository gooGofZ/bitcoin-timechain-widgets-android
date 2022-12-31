package com.googof.bitcointimechainwidgets.mempool

import android.content.Context
import androidx.compose.runtime.Composable
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.googof.bitcointimechainwidgets.AppWidgetBox
import com.googof.bitcointimechainwidgets.AppWidgetColumn
import com.googof.bitcointimechainwidgets.GlanceTheme
import com.googof.bitcointimechainwidgets.R
import com.googof.bitcointimechainwidgets.mempool.lightning.LightningNetworkWorker

class LightningNetworkGlanceWidget : GlanceAppWidget() {

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
                        LightningNetworkCompose(mempoolInfo)
                    }
                }

                is MempoolInfo.Unavailable -> {
                    AppWidgetColumn(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Data not available")
                        Button("Refresh", actionRunCallback<UpdateLightningNetworkAction>())
                    }
                }
            }
        }
    }
}


@Composable
fun LightningNetworkCompose(mempoolInfo: MempoolInfo.Available) {
    Column(
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = GlanceModifier.fillMaxWidth()
            .clickable(actionRunCallback<UpdateBlockHeightAction>()),
    ) {
        Text(
            text = "Lightning Network ⚡",
            style = TextStyle(
                color = ColorProvider(R.color.widget_text_color),
            ),
            modifier = GlanceModifier.defaultWeight()
                .clickable(actionRunCallback<UpdateLightningNetworkAction>())
        )
        //Capacity
        Row(
            modifier = GlanceModifier.wrapContentHeight()
                .fillMaxWidth()
                .clickable(actionRunCallback<UpdateMempoolAction>()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Capacity",
                style = TextStyle(
                    color = ColorProvider(R.color.widget_text_color),
                    textAlign = TextAlign.Center
                ),
                modifier = GlanceModifier.defaultWeight()
                    .clickable(actionRunCallback<UpdateLightningNetworkAction>())
            )

            Text(
                text = "%,d".format(mempoolInfo.ln_total_capacity) + " ₿",
                style = TextStyle(
                    color = ColorProvider(R.color.widget_text_color),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
                modifier = GlanceModifier.defaultWeight()
                    .clickable(actionRunCallback<UpdateLightningNetworkAction>())
            )
        }
        //Channels
        Row(
            modifier = GlanceModifier.wrapContentHeight()
                .fillMaxWidth()
                .clickable(actionRunCallback<UpdateMempoolAction>()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Channels",
                style = TextStyle(
                    color = ColorProvider(R.color.widget_text_color),
                    textAlign = TextAlign.Center,
                ),
                modifier = GlanceModifier.defaultWeight()
                    .clickable(actionRunCallback<UpdateLightningNetworkAction>())
            )

            Text(
                text = "%,d".format(mempoolInfo.ln_channel_count),
                style = TextStyle(
                    color = ColorProvider(R.color.widget_text_color),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
                modifier = GlanceModifier.defaultWeight()
                    .clickable(actionRunCallback<UpdateLightningNetworkAction>())
            )
        }
        //Nodes
        Row(
            modifier = GlanceModifier.wrapContentHeight()
                .fillMaxWidth()
                .clickable(actionRunCallback<UpdateMempoolAction>()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nodes",
                style = TextStyle(
                    color = ColorProvider(R.color.widget_text_color),
                    textAlign = TextAlign.Center,
                ),
                modifier = GlanceModifier.defaultWeight()
                    .clickable(actionRunCallback<UpdateLightningNetworkAction>())
            )

            Text(
                text = "%,d".format(mempoolInfo.ln_node_count),
                style = TextStyle(
                    color = ColorProvider(R.color.widget_text_color),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
                modifier = GlanceModifier.defaultWeight()
                    .clickable(actionRunCallback<UpdateLightningNetworkAction>())
            )
        }
    }
}

/**
 * Force update the info after user click
 */
class UpdateLightningNetworkAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Force the worker to refresh
        LightningNetworkWorker.enqueue(context = context, force = true)
    }
}
