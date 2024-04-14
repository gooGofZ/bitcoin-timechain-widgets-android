package com.googof.bitcointimechainwidgets.widgets.timechain.halving

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.googof.bitcointimechainwidgets.common.estimateDateToHalving
import com.googof.bitcointimechainwidgets.widgets.timechain.TimechainInfo
import com.googof.bitcointimechainwidgets.widgets.timechain.TimechainStateDefinition
import java.time.format.DateTimeFormatter

class NextHalvingWidget : GlanceAppWidget() {

    // Override the state definition to use our custom one using Kotlin serialization
    override val stateDefinition = TimechainStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    @Composable
    fun Content() {
        val timechainInfo = currentState<TimechainInfo>()
        GlanceTheme {
            when (timechainInfo) {
                TimechainInfo.Loading, TimechainInfo.Refreshing -> {
                    CircularProgressIndicator()
                }

                is TimechainInfo.Available -> ContentCompose(timechainInfo)

                is TimechainInfo.Unavailable -> {
                    Row {
                        Text("Data not available")
                        Button("Refresh", actionRunCallback<UpdateAction>())
                    }
                }
            }
        }

    }
}

@Composable
fun ContentCompose(timechain: TimechainInfo.Available) {
    Column(
        modifier = GlanceModifier
            .clickable(actionRunCallback<UpdateAction>())
            .fillMaxWidth()
            .background(GlanceTheme.colors.background)
            .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InfoRow("Next Halving Index", "${timechain.nextHalving.nextHalvingIndex}")
        InfoRow("Next Halving Block", "${timechain.nextHalving.nextHalvingBlock}")
        InfoRow("Next Subsidy", timechain.nextHalving.nextHalvingSubsidy)
        InfoRow("Block Remain", "${timechain.nextHalving.blocksUntilNextHalving}")
        InfoRow("Day Remain", timechain.nextHalving.timeUntilNextHalving)
        InfoRow(
            "Estimated Time",
            estimateDateToHalving(timechain.blockHeight.toInt()).format(
                DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm")
            )
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(4.dp),
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

/**
 * Force update the info after user click
 */
class UpdateAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        // Force the worker to refresh
        NextHalvingWidgetWorker.enqueue(context = context, force = true)
    }
}
