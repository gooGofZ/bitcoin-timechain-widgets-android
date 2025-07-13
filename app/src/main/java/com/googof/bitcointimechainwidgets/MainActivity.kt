package com.googof.bitcointimechainwidgets

import android.appwidget.AppWidgetManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.googof.bitcointimechainwidgets.repository.BitcoinDataRepository
import com.googof.bitcointimechainwidgets.ui.theme.BitcoinTimechainWidgetsTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            // For Android 15+ (API 35), handle edge-to-edge manually
            @Suppress("DEPRECATION")
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            @Suppress("DEPRECATION")
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.attributes.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            }
        } else {
            // Use enableEdgeToEdge for older versions
            enableEdgeToEdge()
        }
        setContent {
            BitcoinTimechainWidgetsTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    "Bitcoin Timechain Widgets",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            actions = {
                                val repository = BitcoinDataRepository(LocalContext.current)
                                val scope = rememberCoroutineScope()
                                val isRefreshOnCooldown by repository.isRefreshOnCooldown.collectAsState(
                                    initial = false
                                )

                                IconButton(
                                    onClick = {
                                        if (!isRefreshOnCooldown) {
                                            scope.launch {
                                                repository.refreshAllData()
                                            }
                                        }
                                    },
                                    enabled = !isRefreshOnCooldown
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = if (isRefreshOnCooldown) "Refresh on cooldown" else "Refresh",
                                        tint = if (isRefreshOnCooldown)
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    DashboardScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

fun formatHalvingDate(dateString: String): String {
    if (dateString.isEmpty()) return ""

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        if (date != null) {
            outputFormat.format(date)
        } else {
            dateString
        }
    } catch (e: Exception) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("d MMMM yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            if (date != null) {
                outputFormat.format(date)
            } else {
                dateString
            }
        } catch (e: Exception) {
            dateString
        }
    }
}

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val repository = remember { BitcoinDataRepository(context) }

    val priceUsd by repository.priceUsd.collectAsState(initial = 0.0)
    val blockHeight by repository.blockHeight.collectAsState(initial = 0)
    val supply by repository.supply.collectAsState(initial = "0")
    val blocksToHalving by repository.blocksToHalving.collectAsState(initial = 0)
    val feeLow by repository.feeLow.collectAsState(initial = 0)
    val feeMed by repository.feeMed.collectAsState(initial = 0)
    val feeHigh by repository.feeHigh.collectAsState(initial = 0)
    val marketCap by repository.marketCap.collectAsState(initial = 0.0)
    val halvingProgress by repository.halvingProgress.collectAsState(initial = 0.0)
    val nextHalvingDate by repository.nextHalvingDate.collectAsState(initial = "")
    val hashrate by repository.hashrate.collectAsState(initial = "0 EH/s")
    val totalNodes by repository.totalNodes.collectAsState(initial = 0)
    val quoteText by repository.quoteText.collectAsState(initial = "")
    val quoteSpeaker by repository.quoteSpeaker.collectAsState(initial = "")

    // Initialize data on first launch
    LaunchedEffect(Unit) {
        repository.refreshAllData()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Version
        item {
            Text(
                text = "v2.1.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        // Block Height Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Block Height",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (blockHeight > 0) blockHeight.toString() else "Loading...",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Fee Priority
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Transaction Fees (sat/vB)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Low", style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = if (feeLow > 0) feeLow.toString() else "--",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("Medium", style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = if (feeMed > 0) feeMed.toString() else "--",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("High", style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = if (feeHigh > 0) feeHigh.toString() else "--",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Moscow Time
        item {
            val satoshisPerUsd = if (priceUsd > 0.0) {
                (100_000_000.0 / priceUsd).toInt()
            } else 0

            val hours = satoshisPerUsd / 100
            val minutes = satoshisPerUsd % 100
            val moscowTime = String.format("%02d:%02d", hours, minutes)

            DataCard(
                title = "Moscow Time",
                value = if (priceUsd > 0) moscowTime else "Loading..."
            )
        }

        // Halving Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Next Halving",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Blocks Remaining: ${if (blocksToHalving > 0) blocksToHalving.toString() else "Loading..."}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Progress: ${
                            if (halvingProgress > 0) String.format(
                                "%.1f%%",
                                halvingProgress
                            ) else "Loading..."
                        }",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (nextHalvingDate.isNotEmpty()) {
                        Text(
                            text = "Est. Date: ${formatHalvingDate(nextHalvingDate)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Network Stats
        items(
            listOf(
                "Supply" to if (supply != "0") supply else "Loading...",
                "Market Cap" to if (marketCap > 0) "$${
                    String.format(
                        "%,.0f",
                        marketCap
                    )
                }" else "Loading...",
                "Hash Rate" to hashrate,
                "Total Nodes" to if (totalNodes > 0) String.format(
                    "%,d",
                    totalNodes
                ) else "Loading..."
            )
        ) { (title, value) ->
            DataCard(title = title, value = value)
        }

        // Price
        item {
            DataCard(
                title = "Price",
                value = if (priceUsd > 0) "$${
                    String.format(
                        "%,.0f",
                        priceUsd
                    )
                }" else "Loading..."
            )
        }

        // Quote
        if (quoteText.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Bitcoin Quote",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"$quoteText\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic
                        )
                        if (quoteSpeaker.isNotEmpty()) {
                            Text(
                                text = "— $quoteSpeaker",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Donation Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Support Development",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "If you find this app useful, consider supporting development with Bitcoin Lightning",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Lightning Address:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "planebeauty04@walletofsatoshi.com",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Button(
                            onClick = {
                                val lightningAddress = "planebeauty04@walletofsatoshi.com"
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip =
                                    ClipData.newPlainText("Lightning Address", lightningAddress)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(
                                    context,
                                    "Lightning address copied to clipboard",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("Copy")
                        }
                    }
                }
            }
        }

        // Widget Launcher Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Add Widgets to Home Screen",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap the button below to add Bitcoin widgets to your home screen",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Text(
                text = "Available Widgets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Widget list
        items(
            listOf(
                "Dashboard" to "com.googof.bitcointimechainwidgets.receiver.DashboardWidgetReceiver",
                "Block Height" to "com.googof.bitcointimechainwidgets.receiver.BlockHeightWidgetReceiver",
                "Price USD" to "com.googof.bitcointimechainwidgets.receiver.PriceUSDWidgetReceiver",
                "Moscow Time" to "com.googof.bitcointimechainwidgets.receiver.MoscowTimeWidgetReceiver",
                "Supply" to "com.googof.bitcointimechainwidgets.receiver.SupplyWidgetReceiver",
                "Blocks to Halving" to "com.googof.bitcointimechainwidgets.receiver.BlocksToNextHalvingWidgetReceiver",
                "Fee Priority" to "com.googof.bitcointimechainwidgets.receiver.FeePriorityWidgetReceiver",
                "Market Cap" to "com.googof.bitcointimechainwidgets.receiver.MarketCapWidgetReceiver",
                "Halving Progress" to "com.googof.bitcointimechainwidgets.receiver.HalvingProgressWidgetReceiver",
                "Next Halving Date" to "com.googof.bitcointimechainwidgets.receiver.NextHalvingDateWidgetReceiver",
                "Days Until Halving" to "com.googof.bitcointimechainwidgets.receiver.DayUntilNextHalvingWidgetReceiver",
                "Hash Rate" to "com.googof.bitcointimechainwidgets.receiver.HashRateWidgetReceiver",
                "Quote" to "com.googof.bitcointimechainwidgets.receiver.QuoteWidgetReceiver"
            )
        ) { (widgetName, receiverClass) ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = widgetName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(
                        onClick = {
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    val appWidgetManager = AppWidgetManager.getInstance(context)
                                    if (appWidgetManager.isRequestPinAppWidgetSupported) {
                                        val myProvider = ComponentName(context, receiverClass)
                                        appWidgetManager.requestPinAppWidget(myProvider, null, null)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add")
                    }
                }
            }
        }

        // API Providers Credit
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "API Providers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Data provided by:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• BitcoinExplorer.org - Block data, supply, fees, halving info, hashrate, quotes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "• Bitnodes.io - Network nodes data",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "• CoinGecko - Price and market cap data",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DataCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    BitcoinTimechainWidgetsTheme {
        DashboardScreen()
    }
}