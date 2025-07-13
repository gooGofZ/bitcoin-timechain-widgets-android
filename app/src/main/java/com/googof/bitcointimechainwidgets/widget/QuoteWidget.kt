package com.googof.bitcointimechainwidgets.widget

import android.content.Context
import android.util.Log
import androidx.compose.ui.unit.dp
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
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontStyle
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.googof.bitcointimechainwidgets.data.quoteDatePreference
import com.googof.bitcointimechainwidgets.data.quoteSpeakerPreferences
import com.googof.bitcointimechainwidgets.data.quoteTextPreference
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import java.text.SimpleDateFormat
import java.util.Locale

private val isLoadingPreference = booleanPreferencesKey("is_loading")

fun formatQuoteDate(dateString: String): String {
    if (dateString.isEmpty()) return ""
    
    return try {
        // First try common date formats that might come from the API
        val possibleFormats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "MM/dd/yyyy"
        )
        
        for (format in possibleFormats) {
            try {
                val inputFormat = SimpleDateFormat(format, Locale.getDefault())
                val outputFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                if (date != null) {
                    return outputFormat.format(date)
                }
            } catch (e: Exception) {
                continue
            }
        }
        
        // If no format works, return the original string
        dateString
    } catch (e: Exception) {
        dateString
    }
}

class RefreshActionQuote : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("QuoteWidget", "RefreshAction triggered")
        try {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = true
            }
            QuoteWidget().update(context, glanceId)

            val quote = BitcoinExplorerApi.create().getQuote()

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[quoteTextPreference] = quote.text
                prefs[quoteSpeakerPreferences] = quote.speaker
                prefs[quoteDatePreference] = quote.date
            }
        } catch (e: Exception) {
            Log.e("QuoteWidget", "Error during refresh", e)
        } finally {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[isLoadingPreference] = false
            }
            QuoteWidget().update(context, glanceId)
        }
    }
}

// QuoteWidget.kt
class QuoteWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val isLoading = prefs[isLoadingPreference] == true

            GlanceTheme {
                Box( // Add a Box to center the LazyColumn
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .padding(8.dp)
                        .clickable(actionRunCallback<RefreshActionQuote>()),
                    contentAlignment = Alignment.Center // Center the content inside the Box
                ) {
                    LazyColumn(
                        modifier = GlanceModifier
                            .wrapContentSize(), // Prevent LazyColumn from stretching
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                    ) {
                        if (isLoading) {
                            item {
                                CircularProgressIndicator(
                                    modifier = GlanceModifier.size(24.dp),
                                    color = GlanceTheme.colors.primary
                                )
                            }
                        } else {
                            item {
                                Text(
                                    text = prefs[quoteTextPreference] ?: "Tap to load quote",
                                    style = TextStyle(
                                        color = GlanceTheme.colors.primary,
                                    ),
                                    modifier = GlanceModifier.padding(8.dp)
                                        .clickable(actionRunCallback<RefreshActionQuote>())
                                )
                            }
                            item {
                                val speaker = prefs[quoteSpeakerPreferences] ?: ""
                                val rawDate = prefs[quoteDatePreference] ?: ""
                                val formattedDate = formatQuoteDate(rawDate)
                                val attribution = if (speaker.isNotEmpty() && formattedDate.isNotEmpty()) {
                                    "$speaker : $formattedDate"
                                } else if (speaker.isNotEmpty()) {
                                    speaker
                                } else if (formattedDate.isNotEmpty()) {
                                    formattedDate
                                } else {
                                    ""
                                }
                                
                                if (attribution.isNotEmpty()) {
                                    Text(
                                        text = attribution,
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
        }
    }
}
