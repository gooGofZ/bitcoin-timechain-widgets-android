package com.googof.bitcointimechainwidgets.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.googof.bitcointimechainwidgets.data.priceUsdPreference
import com.googof.bitcointimechainwidgets.repository.BitcoinDataRepository
import kotlinx.coroutines.flow.first

private val isLoadingPreference = booleanPreferencesKey("is_loading")

class RefreshActionMoscowTime : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("MoscowTimeWidget", "RefreshAction triggered")
        try {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = true
            }
            MoscowTimeWidget().update(context, glanceId)

            val repository = BitcoinDataRepository(context)
            repository.refreshAllData()
            val priceUsd = repository.priceUsd.first()
            Log.d("MoscowTimeWidget", "Price USD: $priceUsd")

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[priceUsdPreference] = priceUsd
            }
        } catch (e: Exception) {
            Log.e("MoscowTimeWidget", "Error during refresh", e)
        } finally {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = false
            }
            MoscowTimeWidget().update(context, glanceId)
        }
    }
}

class MoscowTimeWidget : GlanceAppWidget() {
    @SuppressLint("DefaultLocale")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        try {
            val repository = BitcoinDataRepository(context)
            val priceUsd = repository.priceUsd.first()
            updateAppWidgetState(context, id) { prefs ->
                prefs[priceUsdPreference] = priceUsd
                prefs[isLoadingPreference] = false
            }
        } catch (_: Exception) {
            updateAppWidgetState(context, id) { prefs ->
                prefs[isLoadingPreference] = false
            }
        }

        provideContent {
            val prefs = currentState<Preferences>()
            val priceUsd = prefs[priceUsdPreference] ?: 0.0
            val isLoading = prefs[isLoadingPreference] == true

            val satoshisPerUsd = if (priceUsd > 0.0) {
                (100_000_000.0 / priceUsd).toInt()
            } else 0

            val hours = satoshisPerUsd / 100
            val minutes = satoshisPerUsd % 100
            val timeFormat = String.format("%02d:%02d", hours, minutes)

            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .padding(8.dp)
                        .clickable(actionRunCallback<RefreshActionMoscowTime>()),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = GlanceModifier.size(24.dp),
                            color = GlanceTheme.colors.primary
                        )
                    } else {
                        Text(
                            text = timeFormat,
                            style = TextStyle(
                                color = GlanceTheme.colors.primary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Moscow Time",
                            style = TextStyle(
                                color = GlanceTheme.colors.primary
                            )
                        )
                    }
                }
            }
        }
    }
}