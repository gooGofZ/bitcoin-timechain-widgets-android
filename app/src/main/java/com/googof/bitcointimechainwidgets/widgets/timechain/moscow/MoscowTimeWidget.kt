package com.googof.bitcointimechainwidgets.widgets.timechain.moscow

import android.content.Context
import androidx.compose.runtime.Composable
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
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.googof.bitcointimechainwidgets.common.formatMoscowTime
import com.googof.bitcointimechainwidgets.common.formatNumberWithCommas
import com.googof.bitcointimechainwidgets.widgets.timechain.TimechainInfo
import com.googof.bitcointimechainwidgets.widgets.timechain.TimechainStateDefinition

class MoscowTimeWidget : GlanceAppWidget() {

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
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .clickable(actionRunCallback<UpdateAction>())
    ) {

        Text(
            text = formatMoscowTime(timechain.moscowTime), style = TextStyle(
                color = GlanceTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            )
        )
        Text(
            text = "Moscow Time", style = TextStyle(
                color = GlanceTheme.colors.secondary,
            )
        )
        Text(
            text = formatNumberWithCommas(100000000 / timechain.moscowTime), style = TextStyle(
                color = GlanceTheme.colors.tertiary,
                fontSize = 10.sp,
            )
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
        MoscowTimeWidgetWorker.enqueue(context = context, force = true)
    }
}
