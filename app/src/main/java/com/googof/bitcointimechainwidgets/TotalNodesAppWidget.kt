package com.googof.bitcointimechainwidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.googof.bitcointimechainwidgets.network.BitnodesApi
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class BitnodesAppWidget : AppWidgetProvider() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            CoroutineScope(Dispatchers.Main).launch {
                updateBitnodesAppWidget(context, appWidgetManager, appWidgetId)
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

internal fun convertEpochtoDateTimeString(epoch: Int): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val netDate = Date(epoch.toLong() * 1000)
    return sdf.format(netDate)
}


@RequiresApi(Build.VERSION_CODES.O)
internal suspend fun updateBitnodesAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Call REST API
    val response = BitnodesApi.retrofitService.getTotalNodes()

    // Set response to object
    val snapshotTime = convertEpochtoDateTimeString(response.results[0].timestamp)
    val totalNodes = response.results[0].total_nodes

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.total_nodes_app_widget)

    // Set values to widget
    views.setTextViewText(R.id.textSnapshotTime, snapshotTime)
    views.setTextViewText(R.id.textTotalNodes, "%,d".format(totalNodes))

    // Set last update date time
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val current = LocalDateTime.now().format(formatter)
    views.setTextViewText(R.id.textLastUpdated, current)

    // refresh button
    val intentUpdate = Intent(context, BitnodesAppWidget::class.java)
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