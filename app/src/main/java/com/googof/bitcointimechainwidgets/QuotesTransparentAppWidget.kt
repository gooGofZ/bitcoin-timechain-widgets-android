package com.googof.bitcointimechainwidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import kotlinx.coroutines.*
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class QuotesTransparentAppWidget : AppWidgetProvider() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            CoroutineScope(Dispatchers.Main).launch {
                updateQuotesTransparentAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

@RequiresApi(Build.VERSION_CODES.O)
internal suspend fun updateQuotesTransparentAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Call REST API
    val response = BitcoinExplorerApi.retrofitService.getRandomQuote()

    // Set response to object
    val quoteText = response.text
    val quoteSpeaker = response.speaker
    val quoteDate = response.date

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.quotes_transparent_app_widget)

    // Set values to widget
    views.setTextViewText(R.id.quoteTextTransparent, quoteText)
    views.setTextViewText(R.id.quoteSpeakerTransparent, "$quoteSpeaker : $quoteDate")

    // refresh action
    val intentUpdate = Intent(context, QuotesTransparentAppWidget::class.java)
    intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

    val idArray = intArrayOf(appWidgetId)
    intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray)

    // set up pending intent
    val pendingUpdate = PendingIntent.getBroadcast(
        context,
        appWidgetId,
        intentUpdate,
        PendingIntent.FLAG_IMMUTABLE
    )

    // Set the object able to refresh and get a new random quote.
    views.setOnClickPendingIntent(R.id.quoteTransparentWidget, pendingUpdate)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}