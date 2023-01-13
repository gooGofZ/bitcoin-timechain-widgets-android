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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.googof.bitcointimechainwidgets.AppWidgetBox
import com.googof.bitcointimechainwidgets.AppWidgetColumn
import com.googof.bitcointimechainwidgets.GlanceTheme
import com.googof.bitcointimechainwidgets.R
import com.googof.bitcointimechainwidgets.mempool.transactionfees.TransactionFeesWorker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransactionFeesGlanceWidget : GlanceAppWidget() {

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
                        TransactionFeesCompose(mempoolInfo)
                    }
                }

                is MempoolInfo.Unavailable -> {
                    AppWidgetColumn(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Data not available")
                        Button("Refresh", actionRunCallback<UpdateTransactionFeesAction>())
                    }
                }
            }
        }
    }
}


@Composable
fun TransactionFeesCompose(mempoolInfo: MempoolInfo.Available) {
    Column(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.fillMaxSize()
            .clickable(actionRunCallback<UpdateMempoolAction>()),
    ) {

        //Fees
        Row(
            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth().padding(top = 2.dp)
                .clickable(actionRunCallback<UpdateMempoolAction>()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Low",
                    style = TextStyle(
                        color = GlanceTheme.colors.textColorPrimary,
                        fontSize = 12.sp
                    )
                )
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Medium",
                    style = TextStyle(
                        color = GlanceTheme.colors.textColorPrimary,
                        fontSize = 12.sp
                    )
                )
            }

            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "High",
                    style = TextStyle(
                        color = GlanceTheme.colors.textColorPrimary,
                        fontSize = 12.sp
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
                Text(
                    text = mempoolInfo.hourFee,
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = mempoolInfo.halfHourFee,
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }

            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = mempoolInfo.fastestFee,
                    style = TextStyle(
                        color = GlanceTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
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
                    text = "sat/VB",
                    style = TextStyle(
                        color = GlanceTheme.colors.textColorSecondary,
                        fontSize = 12.sp
                    )
                )
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "sat/VB",
                    style = TextStyle(
                        color = GlanceTheme.colors.textColorSecondary,
                        fontSize = 12.sp
                    )
                )
            }

            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "sat/VB",
                    style = TextStyle(
                        color = GlanceTheme.colors.textColorSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        }
        //Last Update date time
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
                    text = "Updated: " + LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss")),
                    style = TextStyle(
                        color = GlanceTheme.colors.textColorSecondary,
                        fontSize = 10.sp
                    ),
                    modifier = GlanceModifier.clickable(actionRunCallback<UpdateTransactionFeesAction>())
                )
            }
        }
    }
}

/**
 * Force update the info after user click
 */
class UpdateTransactionFeesAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Force the worker to refresh
        TransactionFeesWorker.enqueue(context = context, force = true)
    }
}
