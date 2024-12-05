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
import com.googof.bitcointimechainwidgets.data.blockUntilNextHalvingPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import java.text.NumberFormat

private val isLoadingPreference = booleanPreferencesKey("is_loading")

class RefreshActionBlockUntilNextHalvingWidget : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("BlockUntilNextHalvingWidget", "RefreshAction triggered")
        try {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = true
            }
            BlockUntilNextHalvingWidget().update(context, glanceId)

            val blocksUntilNextHalving =
                BitcoinExplorerApi.create().getNextHalving().blocksUntilNextHalving
            Log.d("BlockUntilNextHalvingWidget", "blocksUntilNextHalving: $blocksUntilNextHalving")

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[blockUntilNextHalvingPreferences] = blocksUntilNextHalving
            }
        } catch (e: Exception) {
            Log.e("BlockUntilNextHalvingWidget", "Error during refresh", e)
        } finally {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = false
            }
            BlockUntilNextHalvingWidget().update(context, glanceId)
        }
    }
}

// BlockUntilNextHalvingWidget.kt
class BlockUntilNextHalvingWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        try {
            val blocksUntilNextHalving =
                BitcoinExplorerApi.create().getNextHalving().blocksUntilNextHalving

            updateAppWidgetState(context, id) { prefs ->
                prefs[blockUntilNextHalvingPreferences] = blocksUntilNextHalving
            }
        } catch (_: Exception) {
        }

        provideContent {
            val prefs = currentState<Preferences>()
            var blocksUntilNextHalving = prefs[blockUntilNextHalvingPreferences] ?: 0

            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .padding(8.dp)
                        .clickable(actionRunCallback<RefreshActionBlockUntilNextHalvingWidget>()),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    Text(
                        text = NumberFormat.getInstance().format(blocksUntilNextHalving),
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = when {
                                blocksUntilNextHalving > 1_000_000 -> 22.sp
                                else -> 24.sp
                            },
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Block Until Halving",
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                        )
                    )
                }
            }
        }
    }
}