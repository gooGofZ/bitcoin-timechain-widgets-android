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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class BitcoinExplorerAppWidget : AppWidgetProvider() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        //val scope = CoroutineScope(newSingleThreadContext("name"))

        for (appWidgetId in appWidgetIds) {
            // GlobalScope.launch { // Delicate API
            // CoroutineScope(Dispatchers.IO).launch { // Work at add and manual refresh
            // GlobalScope.launch(Dispatchers.Main) { // Work at add and manual refresh // Delicate API
            CoroutineScope(Dispatchers.Main).launch {
                updateBitcoinExplorerAppWidget(context, appWidgetManager, appWidgetId)
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
internal suspend fun updateBitcoinExplorerAppWidget(
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
    val views = RemoteViews(context.packageName, R.layout.bitcoinexplorer_app_widget)

    // Set values to widget
    views.setTextViewText(R.id.textSnapshotTime, quoteText)

    // Set last update date time
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val current = LocalDateTime.now().format(formatter)
    views.setTextViewText(R.id.textLastUpdated, current)

    // refresh button
    val intentUpdate = Intent(context, BitcoinExplorerAppWidget::class.java)
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

    views.setOnClickPendingIntent(R.id.buttonRefresh, pendingUpdate)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}