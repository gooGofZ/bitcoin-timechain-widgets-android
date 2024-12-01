package com.googof.bitcointimechainwidgets.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.googof.bitcointimechainwidgets.data.priceUsdPreference
import com.googof.bitcointimechainwidgets.network.MempoolApi

class MoscowTimeWidget : GlanceAppWidget() {
    @SuppressLint("DefaultLocale")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        try {
            val prices = MempoolApi.create().getPrices()
            updateAppWidgetState(context, id) { prefs ->
                prefs[priceUsdPreference] = prices.USD
            }
        } catch (_: Exception) {
            updateAppWidgetState(context, id) { prefs ->
                prefs[priceUsdPreference] = 0
            }
        }
        
        provideContent {
            val prefs = currentState<Preferences>()
            val priceUsd = prefs[priceUsdPreference] ?: 0

            // Calculate satoshis per USD (1 BTC = 100,000,000 satoshis)
            val satoshisPerUsd = if (priceUsd > 0) {
                (100_000_000.0 / priceUsd).toInt()
            } else 0

            println("satoshisPerUsd: $satoshisPerUsd")
            println("priceUsd: $priceUsd")

            // Format as HH:MM
            val hours = satoshisPerUsd / 100  // 1031 / 100 = 10 hours
            val minutes = satoshisPerUsd % 100 // 1031 % 100 = 31 minutes
            val timeFormat = String.format("%02d:%02d", hours, minutes)

            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .padding(16.dp),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
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