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
import com.googof.bitcointimechainwidgets.data.priceThbPreference
import com.googof.bitcointimechainwidgets.network.CoinGeckoApi

private val isLoadingPreference = booleanPreferencesKey("is_loading")

class RefreshActionSiamTime : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("SiamTimeWidget", "RefreshAction triggered")
        try {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = true
            }
            MoscowTimeWidget().update(context, glanceId)

            val priceThb = CoinGeckoApi.create().getTHBPrice().bitcoin.thb

            Log.d("SiamTimeWidget", "THB Price: $priceThb")

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[priceThbPreference] = priceThb
            }
        } catch (e: Exception) {
            Log.e("SiamTimeWidget", "Error during refresh", e)
        } finally {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = false
            }
            MoscowTimeWidget().update(context, glanceId)
        }
    }
}

class SiamTimeWidget : GlanceAppWidget() {
    @SuppressLint("DefaultLocale")
    override suspend fun provideGlance(context: Context, glanceId: GlanceId) {
        try {
            val priceThb = CoinGeckoApi.create().getTHBPrice().bitcoin.thb
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[priceThbPreference] = priceThb
                prefs[isLoadingPreference] = false
            }
        } catch (_: Exception) {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = false
            }
        }

        provideContent {
            val prefs = currentState<Preferences>()
            val priceThb = prefs[priceThbPreference] ?: 0.0
            val isLoading = prefs[isLoadingPreference] == true

            val satoshisPerThb = if (priceThb > 0.0) {
                (100_000_000.0 / priceThb).toInt()
            } else 0

            val hours = satoshisPerThb / 100
            val minutes = satoshisPerThb % 100
            val timeFormat = String.format("%02d:%02d", hours, minutes)

            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .padding(8.dp)
                        .clickable(actionRunCallback<RefreshActionSiamTime>()),
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
                            text = "Siam Time",
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