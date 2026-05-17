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
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.googof.bitcointimechainwidgets.data.blockHeightPreference
import com.googof.bitcointimechainwidgets.data.priceUsdPreference
import com.googof.bitcointimechainwidgets.data.hashRatePreference
import com.googof.bitcointimechainwidgets.data.feeLowPreferences
import com.googof.bitcointimechainwidgets.data.feeMedPreferences
import com.googof.bitcointimechainwidgets.data.feeHighPreferences
import com.googof.bitcointimechainwidgets.data.blocksToNextHalvingPreferences
import com.googof.bitcointimechainwidgets.data.nextHalvingDatePreferences
import com.googof.bitcointimechainwidgets.data.calculateDaysUntilHalving
import com.googof.bitcointimechainwidgets.repository.BitcoinDataRepository
import com.googof.bitcointimechainwidgets.util.formatIsoDate
import kotlinx.coroutines.flow.first
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.googof.bitcointimechainwidgets.worker.DashboardWorker

private val isLoadingPreference = booleanPreferencesKey("dashboard_is_loading")

class RefreshActionDashboard : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        WorkManager.getInstance(context).enqueueUniqueWork(
            "dashboard_update_manual",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<DashboardWorker>().build()
        )
    }
}

class DashboardWidget : GlanceAppWidget() {
    @SuppressLint("DefaultLocale")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        try {
            val repository = BitcoinDataRepository(context)

            // Get all data and store in preferences
            val blockHeight = repository.blockHeight.first()
            val priceUsd = repository.priceUsd.first()
            val hashrate = repository.hashrate.first()
            val feeLow = repository.feeLow.first()
            val feeMed = repository.feeMed.first()
            val feeHigh = repository.feeHigh.first()
            val blocksToHalving = repository.blocksToHalving.first()
            val nextHalvingDate = repository.nextHalvingDate.first()

            updateAppWidgetState(context, id) { prefs ->
                prefs[blockHeightPreference] = blockHeight
                prefs[priceUsdPreference] = priceUsd
                prefs[hashRatePreference] = hashrate
                prefs[feeLowPreferences] = feeLow
                prefs[feeMedPreferences] = feeMed
                prefs[feeHighPreferences] = feeHigh
                prefs[blocksToNextHalvingPreferences] = blocksToHalving
                prefs[nextHalvingDatePreferences] = nextHalvingDate
                prefs[isLoadingPreference] = false
            }
        } catch (_: Exception) {
            updateAppWidgetState(context, id) { prefs ->
                prefs[isLoadingPreference] = false
            }
        }

        provideContent {
            val prefs = currentState<Preferences>()
            val isLoading = prefs[isLoadingPreference] == true

            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .padding(12.dp)
                        .clickable(actionRunCallback<RefreshActionDashboard>()),
                    verticalAlignment = Alignment.Vertical.Top,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                ) {
                    if (isLoading) {
                        Column(
                            modifier = GlanceModifier.fillMaxSize(),
                            verticalAlignment = Alignment.Vertical.CenterVertically,
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = GlanceModifier.size(24.dp),
                                color = GlanceTheme.colors.primary
                            )
                            Spacer(modifier = GlanceModifier.height(6.dp))
                            Text(
                                text = "Updating...",
                                style = TextStyle(
                                    color = GlanceTheme.colors.primary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    } else {

                        // Get data from preferences
                        val blockHeight = prefs[blockHeightPreference] ?: 0
                        val priceUsd = prefs[priceUsdPreference] ?: 0.0
                        val hashrate = prefs[hashRatePreference] ?: "0 EH/s"
                        val feeLow = prefs[feeLowPreferences] ?: 0
                        val feeMed = prefs[feeMedPreferences] ?: 0
                        val feeHigh = prefs[feeHighPreferences] ?: 0
                        val blocksToHalving = prefs[blocksToNextHalvingPreferences] ?: 0
                        val nextHalvingDate = prefs[nextHalvingDatePreferences] ?: ""

                        // Calculate Moscow Time
                        val satoshisPerUsd = if (priceUsd > 0.0) {
                            (100_000_000.0 / priceUsd).toInt()
                        } else 0
                        val hours = satoshisPerUsd / 100
                        val minutes = satoshisPerUsd % 100
                        val moscowTime = String.format("%02d:%02d", hours, minutes)

                        // Parse hashrate for display
                        val parts = hashrate.split(" ")
                        val hashrateValue = parts.getOrNull(0) ?: "..."
                        val hashrateUnit = parts.getOrNull(1) ?: ""

                        // Calculate days until halving
                        val daysUntilHalving = if (nextHalvingDate.isNotEmpty()) {
                            try {
                                calculateDaysUntilHalving(nextHalvingDate)
                            } catch (_: Exception) {
                                0L
                            }
                        } else 0L

                        LazyColumn(
                            modifier = GlanceModifier.fillMaxWidth()
                        ) {
                            // Block Height
                            item {
                                Row(
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Horizontal.Start,
                                    verticalAlignment = Alignment.Vertical.CenterVertically
                                ) {
                                    Text(
                                        text = "Block Height",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                        ),
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                    Text(
                                        text = if (blockHeight > 0) blockHeight.toString() else "...",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.End
                                        )
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = GlanceModifier.height(6.dp))
                            }

                            // Transaction Fees Header
                            item {
                                Text(
                                    text = "Transaction Fees (sat/vB)",
                                    style = TextStyle(
                                        color = GlanceTheme.colors.primary
                                    )
                                )
                            }

                            item {
                                Spacer(modifier = GlanceModifier.height(4.dp))
                            }

                            // Transaction Fees Values
                            item {
                                Row(
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                                ) {
                                    // Low Fee
                                    Column(
                                        modifier = GlanceModifier.defaultWeight(),
                                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (feeLow > 0) feeLow.toString() else "...",
                                            style = TextStyle(
                                                color = GlanceTheme.colors.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "Low",
                                            style = TextStyle(
                                                color = GlanceTheme.colors.primary,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }

                                    // Medium Fee
                                    Column(
                                        modifier = GlanceModifier.defaultWeight(),
                                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (feeMed > 0) feeMed.toString() else "...",
                                            style = TextStyle(
                                                color = GlanceTheme.colors.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "Medium",
                                            style = TextStyle(
                                                color = GlanceTheme.colors.primary,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }

                                    // High Fee
                                    Column(
                                        modifier = GlanceModifier.defaultWeight(),
                                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (feeHigh > 0) feeHigh.toString() else "...",
                                            style = TextStyle(
                                                color = GlanceTheme.colors.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "High",
                                            style = TextStyle(
                                                color = GlanceTheme.colors.primary,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = GlanceModifier.height(6.dp))
                            }

                            // Hash Rate
                            item {
                                Row(
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Horizontal.Start,
                                    verticalAlignment = Alignment.Vertical.CenterVertically
                                ) {
                                    Text(
                                        text = "Hash Rate",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary
                                        ),
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                    Text(
                                        text = "$hashrateValue $hashrateUnit",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.End
                                        )
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = GlanceModifier.height(6.dp))
                            }

                            // Moscow Time
                            item {
                                Row(
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Horizontal.Start,
                                    verticalAlignment = Alignment.Vertical.CenterVertically
                                ) {
                                    Text(
                                        text = "Moscow Time",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                        ),
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                    Text(
                                        text = if (priceUsd > 0) moscowTime else "...",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.End
                                        )
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = GlanceModifier.height(6.dp))
                            }

                            // Price
                            item {
                                Row(
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Horizontal.Start,
                                    verticalAlignment = Alignment.Vertical.CenterVertically
                                ) {
                                    Text(
                                        text = "Price",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary
                                        ),
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                    Text(
                                        text = if (priceUsd > 0) "$${
                                            String.format(
                                                "%,.0f",
                                                priceUsd
                                            )
                                        }" else "...",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.End
                                        )
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = GlanceModifier.height(6.dp))
                            }

                            // Blocks to Halving
                            item {
                                Row(
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Horizontal.Start,
                                    verticalAlignment = Alignment.Vertical.CenterVertically
                                ) {
                                    Text(
                                        text = "Blocks to Halving",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary
                                        ),
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                    Text(
                                        text = if (blocksToHalving > 0) String.format(
                                            "%,d",
                                            blocksToHalving
                                        ) else "...",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.End
                                        )
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = GlanceModifier.height(6.dp))
                            }

                            // Days Until Halving
                            item {
                                Row(
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Horizontal.Start,
                                    verticalAlignment = Alignment.Vertical.CenterVertically
                                ) {
                                    Text(
                                        text = "Days Until Halving",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary
                                        ),
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                    Text(
                                        text = if (daysUntilHalving > 0) String.format(
                                            "%,d",
                                            daysUntilHalving
                                        ) else "...",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.End
                                        )
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = GlanceModifier.height(6.dp))
                            }

                            // Estimate Halving Date
                            item {
                                Row(
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Horizontal.Start,
                                    verticalAlignment = Alignment.Vertical.CenterVertically
                                ) {
                                    Text(
                                        text = "Est. Halving Date",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                        ),
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                    Text(
                                        text = formatIsoDate(nextHalvingDate, "d MMM yyyy").ifEmpty { "..." },
                                        style = TextStyle(
                                            color = GlanceTheme.colors.primary,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.End
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