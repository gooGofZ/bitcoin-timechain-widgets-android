package com.googof.bitcointimechainwidgets.widget

import android.annotation.SuppressLint
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.googof.bitcointimechainwidgets.data.supplyPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi

private val isLoadingPreference = booleanPreferencesKey("is_loading")

class RefreshActionSupply : ActionCallback {
    @SuppressLint("DefaultLocale")
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("SupplyWidget", "RefreshAction triggered")
        try {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = true
            }
            SupplyWidget().update(context, glanceId)

            val supply = BitcoinExplorerApi.create().getSupply().supply

            Log.d("SupplyWidget", "supply: $supply")

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[supplyPreferences] = supply
            }
        } catch (e: Exception) {
            Log.e("SupplyWidget", "Error during refresh", e)
        } finally {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = false
            }
            SupplyWidget().update(context, glanceId)
        }
    }
}

// SupplyWidget.kt
class SupplyWidget : GlanceAppWidget() {
    @SuppressLint("DefaultLocale")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        try {
            val supply = BitcoinExplorerApi.create().getSupply().supply

            updateAppWidgetState(context, id) { prefs ->
                prefs[supplyPreferences] = supply
            }
        } catch (_: Exception) {
        }

        provideContent {
            val prefs = currentState<Preferences>()
            var supply = prefs[supplyPreferences] ?: "..."

            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .padding(8.dp)
                        .clickable(actionRunCallback<RefreshActionSupply>()),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {

                    Text(
                        text = String.format("%,.2f", supply.toBigDecimal()),
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = (String.format(
                            "%,.2f",
                            supply.toBigDecimal() / 21000000.toBigDecimal() * 100.toBigDecimal()
                        )).toString() + "%",
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 16.sp,
                        )
                    )
                    Text(
                        text = "Supply Coins",
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                        )
                    )
                }
            }
        }
    }
}