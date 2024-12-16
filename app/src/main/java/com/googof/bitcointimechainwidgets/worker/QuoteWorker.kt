package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.quoteDatePreference
import com.googof.bitcointimechainwidgets.data.quoteSpeakerPreferences
import com.googof.bitcointimechainwidgets.data.quoteTextPreference
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.widget.QuoteWidget

class QuoteWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        return try {
            val quote = BitcoinExplorerApi.create().getQuote()
            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(QuoteWidget::class.java)
                .firstOrNull()

            glanceId?.let {
                updateAppWidgetState(
                    applicationContext, it
                ) { prefs ->
                    prefs[quoteTextPreference] = quote.text
                    prefs[quoteSpeakerPreferences] = quote.speaker
                    prefs[quoteDatePreference] = quote.date
                }

                QuoteWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("HashRateWorker", "Error updating widget", e)
            Result.retry()
        }
    }
}
