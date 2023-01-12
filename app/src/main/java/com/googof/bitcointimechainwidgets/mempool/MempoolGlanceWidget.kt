package com.googof.bitcointimechainwidgets.mempool

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.*
import com.googof.bitcointimechainwidgets.*
import com.googof.bitcointimechainwidgets.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MempoolGlanceWidget : GlanceAppWidget() {
    companion object {
        private val thinMode = DpSize(120.dp, 120.dp)
        private val smallMode = DpSize(184.dp, 184.dp)
        private val mediumMode = DpSize(260.dp, 200.dp)
        private val largeMode = DpSize(260.dp, 280.dp)
    }

    // Override the state definition to use our custom one using Kotlin serialization
    override val stateDefinition = MempoolInfoStateDefinition

    // Define the supported sizes for this widget.
    // The system will decide which one fits better based on the available space
    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(thinMode, smallMode, mediumMode, largeMode)
    )


    @Composable
    override fun Content() {
        // Get the stored stated based on our custom state definition.
        val mempoolInfo = currentState<MempoolInfo>()

        // It will be one of the provided ones
        //val size = LocalSize.current

        GlanceTheme {
            when (mempoolInfo) {
                MempoolInfo.Loading -> {
                    AppWidgetBox(
                        contentAlignment = Alignment.Center,
                        modifier = GlanceModifier.appWidgetBackground()
                            .background(R.color.widget_background_color)
                    ) {
                        CircularProgressIndicator(color = ColorProvider(R.color.white))//color color = ColorProvider(R.color.widget_text_color))
                    }
                }
                is MempoolInfo.Available -> {
                    AppWidgetColumn(
                    ) {
                        MempoolCompose(mempoolInfo)
                    }
                }

                is MempoolInfo.Unavailable -> {
                    AppWidgetColumn(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Data not available")
                        Button("Refresh", actionRunCallback<UpdateMempoolAction>())
                    }
                }
            }
        }
    }
}

@Composable
fun MempoolCompose(mempoolInfo: MempoolInfo.Available) {
    Column(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.fillMaxSize().clickable(actionRunCallback<UpdateMempoolAction>()),
    ) {
        //Block Height
        Row(
            modifier = GlanceModifier.wrapContentHeight()
                .fillMaxWidth()
                .clickable(actionRunCallback<UpdateMempoolAction>()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight()
                    .clickable(actionRunCallback<UpdateMempoolAction>()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Block Height ",
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_text_color),
                    ),
                )
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = mempoolInfo.blockHeight,
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_text_color),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.clickable(actionRunCallback<UpdateMempoolAction>())
                )
            }
        }
        //Hash Rate
        Row(
            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth()
                .clickable(actionRunCallback<UpdateMempoolAction>()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hash Rate",
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_text_color),
                    )
                )
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(mempoolInfo.currentHashrate / 1000000000000000000).toInt()} EH/s",
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_text_color),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.clickable(actionRunCallback<UpdateMempoolAction>())
                )
            }
        }

        //Total Node
        Row(
            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth()
                .clickable(actionRunCallback<UpdateMempoolAction>()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Node",
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_text_color),
                    )
                )
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "%,d".format(mempoolInfo.totalNode),
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_text_color),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.clickable(actionRunCallback<UpdateMempoolAction>())
                )
            }
        }

        //Unconfirmed TX
        Row(
            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth()
                .clickable(actionRunCallback<UpdateMempoolAction>()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Unconfirmed",
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_text_color),
                    )
                )
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "%,d".format(mempoolInfo.count) + " TXs",
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_text_color),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.clickable(actionRunCallback<UpdateMempoolAction>())
                )
            }
        }

        //Fees
        Row(
            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth().padding(top = 10.dp)
                .clickable(actionRunCallback<UpdateMempoolAction>()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Low Priority",
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_text_color),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Medium Priority",
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_text_color),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }

            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "High Priority",
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_text_color),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
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
                        color = ColorProvider(R.color.widget_text_color),
                        fontWeight = FontWeight.Bold
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
                        color = ColorProvider(R.color.widget_text_color),
                        fontWeight = FontWeight.Bold
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
                        color = ColorProvider(R.color.widget_text_color),
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
                    text = "sat/VB",
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_text_color),
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
                        color = ColorProvider(R.color.widget_text_color),
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
                        color = ColorProvider(R.color.widget_text_color),
                        fontSize = 12.sp
                    )
                )
            }
        }
        //Last Update date time
        Row(
            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth().padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Updated: " + LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    style = TextStyle(
                        color = ColorProvider(R.color.widget_text_color),
                        fontSize = 10.sp
                    ),
                    modifier = GlanceModifier.clickable(actionRunCallback<UpdateMempoolAction>())
                )
            }
        }
    }
}

/**
 * Force update the info after user click
 */
class UpdateMempoolAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Force the worker to refresh
        MempoolWorker.enqueue(context = context, force = true)
    }
}
