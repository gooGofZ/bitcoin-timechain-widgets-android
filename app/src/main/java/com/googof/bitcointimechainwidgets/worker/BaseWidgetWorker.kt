package com.googof.bitcointimechainwidgets.worker

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlin.reflect.KClass

abstract class BaseWidgetWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    abstract val widgetClass: KClass<out GlanceAppWidget>
    abstract val tag: String

    abstract suspend fun fetchAndStore(context: Context, glanceId: GlanceId)

    protected suspend fun glanceId(): GlanceId? =
        GlanceAppWidgetManager(applicationContext)
            .getGlanceIds(widgetClass.java)
            .firstOrNull()

    override suspend fun doWork(): Result {
        return try {
            val glanceId = glanceId() ?: return Result.success()
            fetchAndStore(applicationContext, glanceId)
            widgetClass.java.getDeclaredConstructor().newInstance().update(applicationContext, glanceId)
            Result.success()
        } catch (e: Exception) {
            Log.e(tag, "Error updating widget", e)
            Result.retry()
        }
    }
}
