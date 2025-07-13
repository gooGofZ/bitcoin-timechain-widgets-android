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
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.googof.bitcointimechainwidgets.data.priceUsdPreference
import com.googof.bitcointimechainwidgets.repository.BitcoinDataRepository
import kotlinx.coroutines.flow.first
import java.text.NumberFormat
import java.util.Locale

private val isLoadingPreference = booleanPreferencesKey("is_loading")

class RefreshActionPriceUSD : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("PriceUSDWidget", "RefreshAction triggered")
        try {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = true
            }
            PriceUSDWidget().update(context, glanceId)

            val repository = BitcoinDataRepository(context)
            repository.refreshAllData()
            val priceUsd = repository.priceUsd.first()
            Log.d("PriceUSDWidget", "Price USD: $priceUsd")

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[priceUsdPreference] = priceUsd
            }
        } catch (e: Exception) {
            Log.e("PriceUSDWidget", "Error during refresh", e)
        } finally {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = false
            }
            PriceUSDWidget().update(context, glanceId)
        }
    }
}

// PriceUSDWidget.kt
class PriceUSDWidget : GlanceAppWidget() {
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

            val usdFormat = NumberFormat.getCurrencyInstance(Locale.US).apply {
                maximumFractionDigits = 0
                minimumFractionDigits = 0
            }

            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .padding(8.dp)
                        .clickable(actionRunCallback<RefreshActionPriceUSD>()),
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
                            text = usdFormat.format(priceUsd),
                            style = TextStyle(
                                color = GlanceTheme.colors.primary,
                                fontSize = when {
                                    priceUsd > 1000000 -> 20.sp
                                    priceUsd > 100000 -> 22.sp
                                    else -> 24.sp
                                },
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "USD Price",
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