package com.googof.bitcointimechainwidgets.widget

import android.content.Context
import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.googof.bitcointimechainwidgets.data.marketCapPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi

private val isLoadingPreference = booleanPreferencesKey("is_loading")

fun Double.toTrillions(): String {
    return "$ %.2fT".format(this / 1_000_000_000_000)
}

class RefreshActionMarketCap : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("MarketCapWidget", "RefreshAction triggered")
        try {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = true
            }
            MarketCapWidget().update(context, glanceId)

            val marketCap = BitcoinExplorerApi.create().getMarketCap().usd
            Log.d("MarketCapWidget", "marketCap: $marketCap")

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[marketCapPreferences] = marketCap
            }
        } catch (e: Exception) {
            Log.e("MarketCapWidget", "Error during refresh", e)
        } finally {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = false
            }
            MarketCapWidget().update(context, glanceId)
        }
    }
}

class MarketCapWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val marketCap = prefs[marketCapPreferences] ?: 0.0
            val isLoading = prefs[isLoadingPreference] == true

            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .padding(8.dp)
                        .clickable(actionRunCallback<RefreshActionMarketCap>()),
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
                            text = marketCap.toTrillions(),
                            style = TextStyle(
                                color = GlanceTheme.colors.primary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Market Cap",
                            style = TextStyle(
                                color = GlanceTheme.colors.primary,
                            )
                        )
                    }
                }
            }
        }
    }
}