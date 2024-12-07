package com.googof.bitcointimechainwidgets.widget

import android.content.Context
import android.util.Log
import androidx.compose.ui.unit.TextUnit
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
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.googof.bitcointimechainwidgets.data.feeHighPreferences
import com.googof.bitcointimechainwidgets.data.feeLowPreferences
import com.googof.bitcointimechainwidgets.data.feeMedPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi

private val isLoadingPreference = booleanPreferencesKey("is_loading")

class RefreshActionFeePriority : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("FeePriorityWidget", "RefreshAction triggered")
        try {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = true
            }
            FeePriorityWidget().update(context, glanceId)

            val fees = BitcoinExplorerApi.create().getMempoolFees()

            Log.d("FeesPriorityWidget", "High fees: ${fees.thirtyMin}")
            Log.d("FeesPriorityWidget", "Med fees: ${fees.sixtyMin}")
            Log.d("FeesPriorityWidget", "Low fees: ${fees.oneDay}")

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[feeLowPreferences] = fees.oneDay
                prefs[feeMedPreferences] = fees.sixtyMin
                prefs[feeHighPreferences] = fees.thirtyMin
            }
        } catch (e: Exception) {
            Log.e("FeesPriorityWidget", "Error during refresh", e)
        } finally {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = false
            }
            FeePriorityWidget().update(context, glanceId)
        }
    }
}

private fun calculateFontSize(value: Int): TextUnit {
    return when {
        value > 1000 -> 12.sp
        value > 100 -> 14.sp
        else -> 16.sp
    }
}

// FeePriorityWidget.kt
class FeePriorityWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val feeLow = prefs[feeLowPreferences] ?: 0
            val feeMed = prefs[feeMedPreferences] ?: 0
            var feeHigh = prefs[feeHighPreferences] ?: 0

            val isLoading = prefs[isLoadingPreference] == true

            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .padding(8.dp)
                        .clickable(actionRunCallback<RefreshActionFeePriority>()),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = GlanceModifier.size(24.dp),
                            color = GlanceTheme.colors.primary
                        )
                    } else {

                        Column(
                            modifier = GlanceModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                        ) {
                            Row(
                                modifier = GlanceModifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                            ) {
                                Box(
                                    modifier = GlanceModifier.defaultWeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "High",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                            fontSize = 12.sp,
                                        )
                                    )
                                }
                                Box(
                                    modifier = GlanceModifier.defaultWeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Medium",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                            fontSize = 12.sp,
                                        ),
                                        maxLines = 1
                                    )
                                }
                                Box(
                                    modifier = GlanceModifier.defaultWeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Low",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                            fontSize = 12.sp,
                                        )
                                    )
                                }
                            }
                            Row(
                                modifier = GlanceModifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                            ) {
                                Box(
                                    modifier = GlanceModifier.defaultWeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = feeHigh.toString(),
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = calculateFontSize(feeHigh)
                                        )
                                    )
                                }
                                Box(
                                    modifier = GlanceModifier.defaultWeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = feeMed.toString(),
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = calculateFontSize(feeHigh)
                                        )
                                    )
                                }
                                Box(
                                    modifier = GlanceModifier.defaultWeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = feeLow.toString(),
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = calculateFontSize(feeHigh)
                                        )
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Fee Priority (sat/vB)",
                            style = TextStyle(
                                color = GlanceTheme.colors.primary,
                                fontSize = 12.sp,
                            ),
                        )
                    }
                }
            }
        }
    }
}