package com.googof.bitcointimechainwidgets.widgets.timechain.summary

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.googof.bitcointimechainwidgets.common.formatMoscowTime
import com.googof.bitcointimechainwidgets.widgets.timechain.TimechainInfo
import com.googof.bitcointimechainwidgets.widgets.timechain.TimechainStateDefinition
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SummaryWidget : GlanceAppWidget() {

    // Override the state definition to use our custom one using Kotlin serialization
    override val stateDefinition = TimechainStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    @Composable
    fun Content() {
        val timechain = currentState<TimechainInfo>()
        GlanceTheme {
            when (timechain) {
                TimechainInfo.Loading, TimechainInfo.Refreshing -> {
                    CircularProgressIndicator()
                }

                is TimechainInfo.Available -> MempoolCompose(timechain)

                is TimechainInfo.Unavailable -> {
                    Row {
                        Text("Data not available")
                        Button("Refresh", actionRunCallback<UpdateSummaryWidgetAction>())
                    }
                }
            }
        }

    }

    //    @Composable
//    fun MempoolCompose(mempool: Mempool.Available) {
//        //Block Height
//        Row(
//            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth()
//                .clickable(actionRunCallback<UpdateSummaryWidgetAction>()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Block Height ",
//                    style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                    ),
//                )
//            }
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(//Block Height Text
//                    text = mempool.blockHeight,
//                    style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                        fontWeight = FontWeight.Bold
//                    ),
//                    modifier = GlanceModifier.clickable(actionRunCallback<UpdateSummaryWidgetAction>())
//                )
//            }
//        }
//        //Hash Rate
//        Row(
//            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth()
//                .clickable(actionRunCallback<UpdateSummaryWidgetAction>()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Hash Rate", style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                    )
//                )
//            }
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text( //Hash Rate Text
//                    text = "${(mempool.currentHashrate / 1000000000000000000).toInt()} EH/s",
//                    style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                        fontWeight = FontWeight.Bold
//                    ),
//                    modifier = GlanceModifier.clickable(actionRunCallback<UpdateSummaryWidgetAction>())
//                )
//            }
//        }
//
//        //Total Node
//        Row(
//            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth()
//                .clickable(actionRunCallback<UpdateSummaryWidgetAction>()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Total Node", style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                    )
//                )
//            }
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text( //Total Node Text
//                    text = "%,d".format(mempool.totalNode),
//                    style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                        fontWeight = FontWeight.Bold
//                    ),
//                    modifier = GlanceModifier.clickable(actionRunCallback<UpdateSummaryWidgetAction>())
//                )
//            }
//        }
//
//        //Unconfirmed TX
//        Row(
//            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth()
//                .clickable(actionRunCallback<UpdateSummaryWidgetAction>()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Unconfirmed", style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                    )
//                )
//            }
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text( //Unconfirmed
//                    text = "%,d".format(mempool.count) + " TXs",
//                    style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                        fontWeight = FontWeight.Bold
//                    ),
//                    modifier = GlanceModifier.clickable(actionRunCallback<UpdateSummaryWidgetAction>())
//                )
//            }
//        }
//
//        //Fees
//        Row(
//            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth().padding(top = 10.dp)
//                .clickable(actionRunCallback<UpdateSummaryWidgetAction>()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Low Priority", style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                        fontSize = 12.sp,
//                        textAlign = TextAlign.Center
//                    )
//                )
//            }
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Medium Priority", style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                        fontSize = 12.sp,
//                        textAlign = TextAlign.Center
//                    )
//                )
//            }
//
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "High Priority", style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                        fontSize = 12.sp,
//                        textAlign = TextAlign.Center
//                    )
//                )
//            }
//        }
//        //Fees Values
//        Row(
//            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text( //Low
//                    text = mempool.hourFee, style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                        fontWeight = FontWeight.Bold
//                    )
//                )
//            }
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text( //Medium
//                    text = mempool.halfHourFee, style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                        fontWeight = FontWeight.Bold
//                    )
//                )
//            }
//
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text( //High
//                    text = mempool.fastestFee, style = TextStyle(
//                        color = GlanceTheme.colors.primary,
//                        fontWeight = FontWeight.Bold
//                    )
//                )
//            }
//        }
//
//        //Fees unit
//        Row(
//            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "sat/VB", style = TextStyle(
//                        color = GlanceTheme.colors.primary, fontSize = 12.sp
//                    )
//                )
//            }
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "sat/VB", style = TextStyle(
//                        color = GlanceTheme.colors.primary, fontSize = 12.sp
//                    )
//                )
//            }
//
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "sat/VB", style = TextStyle(
//                        color = GlanceTheme.colors.primary, fontSize = 12.sp
//                    )
//                )
//            }
//        }
//        //Last Update date time
//        Row(
//            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth().padding(top = 10.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Column(
//                modifier = GlanceModifier.defaultWeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Updated: " + LocalDateTime.now()
//                        .format(DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss")),
//                    style = TextStyle(
//                        color = GlanceTheme.colors.primary, fontSize = 10.sp
//                    ),
//                    modifier = GlanceModifier.clickable(actionRunCallback<UpdateSummaryWidgetAction>())
//                )
//            }
//        }
//    }
//
    @Composable
    fun MempoolCompose(timechain: TimechainInfo.Available) {
        Column(
            modifier = GlanceModifier
                .clickable(actionRunCallback<UpdateSummaryWidgetAction>())
                .fillMaxWidth()
                .background(GlanceTheme.colors.background)
                .padding(8.dp)
        ) {
            InfoRow("Block Height", timechain.blockHeight)
            InfoRow("Moscow Time", formatMoscowTime(timechain.moscowTime))
            InfoRow(
                "Hash Rate",
                "${(timechain.currentHashrate / 1000000000000000000).toInt()} EH/s"
            )
            InfoRow("Unconfirmed", "${timechain.count} TXs")
            FeeRow(timechain)
            LastUpdateRow()
        }
    }

    @Composable
    fun InfoRow(label: String, value: String) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = TextStyle(
                    color = GlanceTheme.colors.secondary,
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            Text(
                text = value,
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = GlanceModifier
            )
        }
    }

    @Composable
    fun FeeRow(timechain: TimechainInfo.Available) {
        //Fees
        Row(
            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Low Priority", style = TextStyle(
                        color = GlanceTheme.colors.secondary, fontSize = 12.sp
                    )
                )
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Medium Priority", style = TextStyle(
                        color = GlanceTheme.colors.secondary, fontSize = 12.sp
                    ),
                    maxLines = 1
                )
            }

            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "High Priority", style = TextStyle(
                        color = GlanceTheme.colors.secondary, fontSize = 12.sp
                    )
                )
            }
        }
        //Fees Values
        Row(
            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text( //Low
                    text = timechain.hourFee, style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text( //Medium
                    text = timechain.halfHourFee, style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text( //High
                    text = timechain.fastestFee, style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        //Fees unit
        Row(
            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "sat/vB", style = TextStyle(
                        color = GlanceTheme.colors.secondary, fontSize = 12.sp
                    )
                )
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "sat/vB", style = TextStyle(
                        color = GlanceTheme.colors.secondary, fontSize = 12.sp
                    )
                )
            }

            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "sat/vB", style = TextStyle(
                        color = GlanceTheme.colors.secondary, fontSize = 12.sp
                    )
                )
            }
        }
    }

    @Composable
    fun LastUpdateRow() {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Updated: ${
                    LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss"))
                }",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = GlanceTheme.colors.tertiary
                )
            )
        }
    }
}

/**
 * Force update the info after user click
 */
class UpdateSummaryWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context, glanceId: GlanceId, parameters: ActionParameters,
    ) {
        // Force the worker to refresh
        SummaryWidgetWorker.enqueue(context = context, force = true)
    }
}