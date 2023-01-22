package com.googof.bitcointimechainwidgets.mempool.halving

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.currentState
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
import com.googof.bitcointimechainwidgets.mempool.MempoolInfo
import com.googof.bitcointimechainwidgets.mempool.MempoolInfoStateDefinition
import com.googof.bitcointimechainwidgets.common.*
import java.time.format.DateTimeFormatter

class NextHalvingGlanceWidget : GlanceAppWidget() {

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
                        NextHalvingCompose(mempoolInfo)
                    }
                }

                is MempoolInfo.Unavailable -> {
                    AppWidgetColumn(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Data not available")
                        Button("Refresh", actionRunCallback<UpdateNextHalvingAction>())
                    }
                }
            }
        }
    }
}

@Composable
fun NextHalvingCompose(mempoolInfo: MempoolInfo.Available) {
    Column(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.fillMaxSize()
            .clickable(actionRunCallback<UpdateNextHalvingAction>())
    ) {
        //Header Widget
        Row(
            modifier = GlanceModifier.wrapContentHeight()
                .fillMaxWidth()
                .clickable(actionRunCallback<UpdateNextHalvingAction>()),
        ) {
            Text(
                text = "Next ${nextBlockHalving(mempoolInfo.blockHeight.toInt())} Halving",
                style = TextStyle(
                    color = GlanceTheme.colors.textColorPrimary,
                    textAlign = TextAlign.Center,
                ),
                modifier = GlanceModifier.defaultWeight()
                    .clickable(actionRunCallback<UpdateNextHalvingAction>()),
            )
        }
        //Header Text
        Row(
            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth()
                .clickable(actionRunCallback<UpdateNextHalvingAction>()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Blocks Remain",
                    style = TextStyle(
                        color = GlanceTheme.colors.textColorPrimary,
                        fontSize = 12.sp,
                    )
                )
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Days Remain",
                    style = TextStyle(
                        color = GlanceTheme.colors.textColorPrimary,
                        fontSize = 12.sp,
                    )
                )
            }

            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Est. Date",
                    style = TextStyle(
                        color = GlanceTheme.colors.textColorPrimary,
                        fontSize = 12.sp,
                    )
                )
            }
        }
        //Value
        Row(
            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth()
                .clickable(actionRunCallback<UpdateNextHalvingAction>()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = GlanceModifier.defaultWeight().fillMaxHeight()
                    .clickable(actionRunCallback<UpdateNextHalvingAction>()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${remainBlockToHalving(mempoolInfo.blockHeight.toInt())}",
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
            Box(
                modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = daysToHalving(mempoolInfo.blockHeight.toInt()),
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }

            Box(
                modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = estimateDateToHalving(mempoolInfo.blockHeight.toInt()).format(
                        DateTimeFormatter.ofPattern("yyyy MMM dd\r\nHH:mm")
                    ),
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

class UpdateNextHalvingAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        //Force the worker to refresh
        NextHalvingWorker.enqueue(context = context, force = true)

    }
}