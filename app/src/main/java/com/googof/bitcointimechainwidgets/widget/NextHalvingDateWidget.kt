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
import com.googof.bitcointimechainwidgets.data.convertToLocalDateTime
import com.googof.bitcointimechainwidgets.data.nextHalvingDatePreferences
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import java.time.format.DateTimeFormatter

private val isLoadingPreference = booleanPreferencesKey("is_loading")

class RefreshActionHalvingProgress : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("NextHalvingDateWidget", "RefreshAction triggered")
        try {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = true
            }
            NextHalvingDateWidget().update(context, glanceId)

            val nextHalvingEstimatedDate =
                BitcoinExplorerApi.create().getNextHalving().nextHalvingEstimatedDate

            Log.d(
                "NextHalvingDateWidget", nextHalvingEstimatedDate
            )

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[nextHalvingDatePreferences] =
                    nextHalvingEstimatedDate
            }
        } catch (e: Exception) {
            Log.e("NextHalvingDateWidget", "Error during refresh", e)
        } finally {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = false
            }
            NextHalvingDateWidget().update(context, glanceId)
        }
    }
}

class NextHalvingDateWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val isLoading = prefs[isLoadingPreference] == true

            var nextHalvingDate = prefs[nextHalvingDatePreferences] ?: "..."
            var nextHalvingTime = "..."

            if (nextHalvingDate != "...") {
                val nextHalvingLocalDate = convertToLocalDateTime(nextHalvingDate)
                val formatterYM = DateTimeFormatter.ofPattern("dd MMM yyyy")
                val formatterDT = DateTimeFormatter.ofPattern("HH:mm")
                nextHalvingDate = nextHalvingLocalDate.format(formatterYM)
                nextHalvingTime = nextHalvingLocalDate.format(formatterDT)
            }
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .padding(8.dp)
                        .clickable(actionRunCallback<RefreshActionHalvingProgress>()),
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
                            text = nextHalvingDate,
                            style = TextStyle(
                                color = GlanceTheme.colors.primary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = nextHalvingTime,
                            style = TextStyle(
                                color = GlanceTheme.colors.primary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Next Halving Date",
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