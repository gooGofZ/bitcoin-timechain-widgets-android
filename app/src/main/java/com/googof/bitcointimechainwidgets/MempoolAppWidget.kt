package com.googof.bitcointimechainwidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.googof.bitcointimechainwidgets.network.MempoolApi
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of App Widget functionality.
 */
class MempoolAppWidget : AppWidgetProvider() {
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
                updateMempoolAppWidget(context, appWidgetManager, appWidgetId)
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
internal suspend fun updateMempoolAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Call REST API
    val blockHeight = MempoolApi.retrofitService.getBlockTipHeight()
    val fees = MempoolApi.retrofitService.getRecommendedFee()
    val hashRate =  MempoolApi.retrofitService.getHashrate()
    val unconfirmedTX = MempoolApi.retrofitService.getUncomfirmedTX()

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.mempool_app_widget)

    // Set Block Details
    views.setTextViewText(R.id.textBlockHeight, blockHeight)

    // Set Transaction Fees
    views.setTextViewText(R.id.textHighPriority, fees.fastestFee)
    views.setTextViewText(R.id.textHalfHourFee, fees.halfHourFee)
    views.setTextViewText(R.id.textHourFee, fees.hourFee)

    // Set Hashrate and difficulty EH/s
    val currentHashrate = hashRate.currentHashrate / (1000000000000000000) //EH/s
    val currentHashrateText = currentHashrate.toInt().toString() + " " + "EH/s"
    views.setTextViewText(R.id.textHashRate, currentHashrateText)

    // Set last update date time
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val current = LocalDateTime.now().format(formatter)
    views.setTextViewText(R.id.textLastUpdated, current)

    // Set Unconfirmed Transactions
    val unconfirmedTXCount = unconfirmedTX.count
    views.setTextViewText(R.id.textUnconfirmedTX, "%,d".format(unconfirmedTXCount) + " TXs")

    // refresh button
    val intentUpdate = Intent(context, MempoolAppWidget::class.java)
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

    views.setOnClickPendingIntent(R.id.layoutMempoolWidget, pendingUpdate)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}