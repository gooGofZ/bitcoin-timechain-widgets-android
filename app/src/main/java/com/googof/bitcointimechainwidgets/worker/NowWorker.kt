package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.googof.bitcointimechainwidgets.data.nowPreferences
import com.googof.bitcointimechainwidgets.widget.NowWidget
import java.time.LocalDateTime

class NowWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("NowWorker", "Worker running")

        return try {
            val glanceId = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(NowWidget::class.java).firstOrNull()

            glanceId?.let {
                updateAppWidgetState(applicationContext, it) { prefs ->
                    prefs[nowPreferences] = LocalDateTime.now().toString()
                }
                NowWidget().update(applicationContext, it)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("NowWorker", "Error updating widget", e)
            Result.retry()
        }
    }
}
