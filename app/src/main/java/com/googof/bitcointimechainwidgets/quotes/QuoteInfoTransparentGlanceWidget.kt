package com.googof.bitcointimechainwidgets.quotes

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

class QuoteInfoTransparentGlanceWidget : GlanceAppWidget() {

    // Override the state definition to use our custom one using Kotlin serialization
    override val stateDefinition = QuoteInfoStateDefinition

    @Composable
    override fun Content() {
        // Get the stored stated based on our custom state definition.
        val quoteInfo = currentState<QuoteInfo>()

        GlanceTheme {
            when (quoteInfo) {
                QuoteInfo.Loading -> {
                    Column(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(color = ColorProvider(R.color.white))
                    }
                }
                is QuoteInfo.Available -> {
                    Column(
                    ) {
                        QuoteTransparentCompose(quoteInfo)
                    }
                }

                is QuoteInfo.Unavailable -> {
                    Column(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Data not available")
                        Button("Refresh", actionRunCallback<UpdateQuoteAction>())
                    }
                }
            }
        }
    }
}

@Composable
fun QuoteTransparentCompose(quoteInfo: QuoteInfo.Available) {
    Column(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.fillMaxSize().clickable(actionRunCallback<UpdateQuoteTransparentAction>()),
    ) {
        Text(
            text = quoteInfo.text,
            style = TextStyle(
                color = ColorProvider(R.color.widget_text_color),
            ),
            modifier = GlanceModifier.clickable(actionRunCallback<UpdateQuoteTransparentAction>())
        )
        Text(
            text = "${quoteInfo.speaker} : ${quoteInfo.date}",
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

class UpdateQuoteTransparentAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Force the worker to refresh
        QuoteInfoTransparentWorker.enqueue(context = context, force = true)
    }
}