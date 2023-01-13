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
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.layout.*
import androidx.glance.text.*
import com.googof.bitcointimechainwidgets.*

class QuoteInfoGlanceWidget : GlanceAppWidget() {

    // Override the state definition to use our custom one using Kotlin serialization
    override val stateDefinition = QuoteInfoStateDefinition

    @Composable
    override fun Content() {
        // Get the stored stated based on our custom state definition.
        val quoteInfo = currentState<QuoteInfo>()

        GlanceTheme {
            when (quoteInfo) {
                QuoteInfo.Loading -> {
                    AppWidgetBox(
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is QuoteInfo.Available -> {
                    AppWidgetColumn(
                    ) {
                        QuoteCompose(quoteInfo)
                    }
                }

                is QuoteInfo.Unavailable -> {
                    AppWidgetColumn(
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
fun QuoteCompose(quoteInfo: QuoteInfo.Available) {
    Column(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.fillMaxSize().clickable(actionRunCallback<UpdateQuoteAction>()),
    ) {
        LazyColumn {
            item {
                Text(
                    text = quoteInfo.text,
                    style = TextStyle(
                        color = GlanceTheme.colors.textColorPrimary,
                    ),
                    modifier = GlanceModifier.clickable(actionRunCallback<UpdateQuoteAction>())
                )
            }
            item {
                Text(
                    text = "${quoteInfo.speaker} : ${quoteInfo.date}",
                    style = TextStyle(
                        color = GlanceTheme.colors.textColorPrimary,
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Right
                    ),
                    modifier = GlanceModifier.fillMaxWidth()
                )
            }
        }
    }
}

class UpdateQuoteAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Force the worker to refresh
        QuoteInfoWorker.enqueue(context = context, force = true)
    }
}