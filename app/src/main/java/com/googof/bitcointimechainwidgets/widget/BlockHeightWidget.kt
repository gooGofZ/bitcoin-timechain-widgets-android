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
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.googof.bitcointimechainwidgets.data.blockHeightPreference
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi

// BlockHeightWidget.kt
class BlockHeightWidget : GlanceAppWidget() {
    class RefreshAction : ActionCallback {
        override suspend fun onAction(
            context: Context,
            glanceId: GlanceId,
            parameters: ActionParameters
        ) {
            try {
                val blockHeight = BitcoinExplorerApi.create().getLatestBlock().height
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[blockHeightPreference] = blockHeight
                }
                PriceUSDWidget().update(context, glanceId)
            } catch (_: Exception) {
            }
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        try {
            val blockHeight = BitcoinExplorerApi.create().getLatestBlock().height

            updateAppWidgetState(context, id) { prefs ->
                prefs[blockHeightPreference] = blockHeight
            }
        } catch (_: Exception) {
        }

        provideContent {
            val prefs = currentState<Preferences>()
            var blockHeight = prefs[blockHeightPreference] ?: 0

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
                        text = blockHeight.toString(),
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = when {
                                blockHeight > 1000000 -> 22.sp
                                else -> 24.sp
                            },
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Block Height",
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
//                            fontSize = 16.sp,
                        )
                    )
                }
            }
        }
    }
}