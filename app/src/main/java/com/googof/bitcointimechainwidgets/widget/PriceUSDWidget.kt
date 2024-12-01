package com.googof.bitcointimechainwidgets.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
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
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import java.text.NumberFormat
import java.util.*

class PriceUSDWidget : GlanceAppWidget() {
    class RefreshAction : ActionCallback {
        override suspend fun onAction(
            context: Context,
            glanceId: GlanceId,
            parameters: ActionParameters
        ) {
            try {
                val prices = BitcoinExplorerApi.create().getPrice()
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[priceUsdPreference] = prices.usd.toInt()
                }
                PriceUSDWidget().update(context, glanceId)
            } catch (_: Exception) {
            }
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        try {
            val prices = BitcoinExplorerApi.create().getPrice()
            updateAppWidgetState(context, id) { prefs ->
                prefs[priceUsdPreference] = prices.usd.toInt()
            }
        } catch (_: Exception) {
            updateAppWidgetState(context, id) { prefs ->
                prefs[priceUsdPreference] = 0
            }
        }
        provideContent {
            val prefs = currentState<Preferences>()
            val priceUsd = prefs[priceUsdPreference] ?: 0

            val usdFormat = NumberFormat.getCurrencyInstance(Locale.US).apply {
                maximumFractionDigits = 0
                minimumFractionDigits = 0
            }

            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .padding(16.dp)
                        .clickable { actionRunCallback<RefreshAction>() },
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    Text(
                        text = usdFormat.format(priceUsd),
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = when {
                                priceUsd > 1000000 -> 18.sp
                                priceUsd > 100000 -> 24.sp
                                else -> 18.sp
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
