package com.googof.bitcointimechainwidgets.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
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
import com.googof.bitcointimechainwidgets.data.priceUsdPreference
import com.googof.bitcointimechainwidgets.network.MempoolApi

// BlockHeightWidget.kt
class BlockHeightWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        try {
            val blockHeight = MempoolApi.create().getBlockHeight()

            updateAppWidgetState(context, id) { prefs ->
                prefs[blockHeightPreference] = blockHeight
            }
        } catch (_: Exception) {
            updateAppWidgetState(context, id) { prefs ->
                prefs[blockHeightPreference] = 0
            }
        }
        
        provideContent {
            val prefs = currentState<Preferences>()
            val blockHeight = prefs[blockHeightPreference] ?: 0

            println("Block Height: $blockHeight")
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
                        text = blockHeight.toString(),
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 24.sp,
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