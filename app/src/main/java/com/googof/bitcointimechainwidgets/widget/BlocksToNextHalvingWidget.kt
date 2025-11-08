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
import com.googof.bitcointimechainwidgets.data.blocksToNextHalvingPreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi

private val isLoadingPreference = booleanPreferencesKey("is_loading")

class RefreshActionBlocksToNextHalvingWidget : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("BlocksToNextHalvingWidget", "RefreshAction triggered")
        try {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = true
            }
            BlocksToNextHalvingWidget().update(context, glanceId)

            val blocksUntilNextHalving =
                BitcoinExplorerApi.create().getNextHalving().blocksUntilNextHalving
            Log.d("BlocksToNextHalvingWidget", "blocksUntilNextHalving: $blocksUntilNextHalving")

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[blocksToNextHalvingPreferences] = blocksUntilNextHalving
            }
        } catch (e: Exception) {
            Log.e("BlocksToNextHalvingWidget", "Error during refresh", e)
        } finally {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = false
            }
            BlocksToNextHalvingWidget().update(context, glanceId)
        }
    }
}

// BlocksToNextHalvingWidget.kt
class BlocksToNextHalvingWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val blocksUntilNextHalving = prefs[blocksToNextHalvingPreferences] ?: 0
            val isLoading = prefs[isLoadingPreference] == true

            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .padding(4.dp)
                        .clickable(actionRunCallback<RefreshActionBlocksToNextHalvingWidget>()),
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
                            text = blocksUntilNextHalving.toString(),
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
                            text = "Blocks To Halving",
                            style = TextStyle(
                                color = GlanceTheme.colors.primary,
//                                fontSize = 18.sp
                            )
                        )
                    }
                }
            }
        }
    }
}