package com.googof.bitcointimechainwidgets.satoshiquotes

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
import androidx.glance.text.*
import androidx.glance.unit.*
import com.googof.bitcointimechainwidgets.*
import com.googof.bitcointimechainwidgets.R
import androidx.glance.appwidget.lazy.LazyColumn

class SatoshiQuoteGlanceWidget : GlanceAppWidget() {

    override val stateDefinition = SatoshiQuoteStateDefinition

    @Composable
    override fun Content() {
        val satoshiQuoteInfo = currentState<SatoshiQuoteInfo>()

        GlanceTheme() {
            when (satoshiQuoteInfo) {
                SatoshiQuoteInfo.Loading -> {
                    AppWidgetBox(
                        contentAlignment = Alignment.Center,
                        modifier = GlanceModifier.appWidgetBackground()
                            .background(R.color.widget_background_color)
                    ) {
                        CircularProgressIndicator(color = ColorProvider(R.color.white))
                    }
                }
                is SatoshiQuoteInfo.Available -> {
                    AppWidgetColumn(
                        modifier = GlanceModifier.appWidgetBackground()
                            .background(R.color.widget_background_color)
                    ) {
                        SatoshiQuoteCompose(satoshiQuoteInfo)
                    }
                }
                is SatoshiQuoteInfo.Unavailable -> {
                    AppWidgetColumn(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Data not available")
                        Button("Refresh", actionRunCallback<UpdateSatoshiQuoteAction>())
                    }
                }
            }
        }
    }
}

@Composable
fun SatoshiQuoteCompose(satoshiQuote: SatoshiQuoteInfo.Available) {
    Column(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.fillMaxSize()
            .clickable(actionRunCallback<UpdateSatoshiQuoteAction>()),
    ) {

        Text(
            text = "Satoshi's Quote",
            style = TextStyle(
                color = ColorProvider(R.color.widget_text_color),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = GlanceModifier.clickable(actionRunCallback<UpdateSatoshiQuoteAction>())
        )
        Text(
            text = satoshiQuote.text,
            style = TextStyle(
                color = ColorProvider(R.color.widget_text_color),
            ),
            modifier = GlanceModifier.clickable(actionRunCallback<UpdateSatoshiQuoteAction>())
        )
        Text(
            text = "${satoshiQuote.category} : ${satoshiQuote.date}",
            style = TextStyle(
                fontSize = 12.sp,
                color = ColorProvider(R.color.widget_text_color),
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Right
            ),
            modifier = GlanceModifier.fillMaxWidth()
        )
    }
}

class UpdateSatoshiQuoteAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Force the worker to refresh
        SatoshiQuoteWorker.enqueue(context = context, force = true)
    }
}