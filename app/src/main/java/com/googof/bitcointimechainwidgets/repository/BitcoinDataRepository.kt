package com.googof.bitcointimechainwidgets.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import android.util.Log
import com.googof.bitcointimechainwidgets.data.BLOCKS_PER_HALVING
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.network.CoinGeckoApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bitcoin_data")

class BitcoinDataRepository(private val context: Context) {

    // DataStore keys
    companion object {
        private const val TAG = "BitcoinDataRepository"
        val PRICE_USD_KEY = doublePreferencesKey("price_usd")
        val PRICE_THB_KEY = doublePreferencesKey("price_thb")
        val BLOCK_HEIGHT_KEY = intPreferencesKey("block_height")
        val SUPPLY_KEY = stringPreferencesKey("supply")
        val BLOCKS_TO_HALVING_KEY = intPreferencesKey("blocks_to_halving")
        val FEE_LOW_KEY = intPreferencesKey("fee_low")
        val FEE_MED_KEY = intPreferencesKey("fee_med")
        val FEE_HIGH_KEY = intPreferencesKey("fee_high")
        val MARKET_CAP_KEY = doublePreferencesKey("market_cap")
        val HALVING_PROGRESS_KEY = doublePreferencesKey("halving_progress")
        val NEXT_HALVING_DATE_KEY = stringPreferencesKey("next_halving_date")
        val HASHRATE_KEY = stringPreferencesKey("hashrate")
        val QUOTE_TEXT_KEY = stringPreferencesKey("quote_text")
        val QUOTE_SPEAKER_KEY = stringPreferencesKey("quote_speaker")
        val QUOTE_DATE_KEY = stringPreferencesKey("quote_date")
        val LAST_REFRESH_TIME_KEY = longPreferencesKey("last_refresh_time")
        
        const val REFRESH_COOLDOWN_DURATION = 30000L // 30 seconds in milliseconds
    }

    // Network APIs
    private val bitcoinApi = BitcoinExplorerApi.instance
    private val coinGeckoApi = CoinGeckoApi.instance

    // Data flows
    val priceUsd: Flow<Double> = context.dataStore.data.map { it[PRICE_USD_KEY] ?: 0.0 }
    val priceThb: Flow<Double> = context.dataStore.data.map { it[PRICE_THB_KEY] ?: 0.0 }
    val blockHeight: Flow<Int> = context.dataStore.data.map { it[BLOCK_HEIGHT_KEY] ?: 0 }
    val supply: Flow<String> = context.dataStore.data.map { it[SUPPLY_KEY] ?: "0" }
    val blocksToHalving: Flow<Int> = context.dataStore.data.map { it[BLOCKS_TO_HALVING_KEY] ?: 0 }
    val feeLow: Flow<Int> = context.dataStore.data.map { it[FEE_LOW_KEY] ?: 0 }
    val feeMed: Flow<Int> = context.dataStore.data.map { it[FEE_MED_KEY] ?: 0 }
    val feeHigh: Flow<Int> = context.dataStore.data.map { it[FEE_HIGH_KEY] ?: 0 }
    val marketCap: Flow<Double> = context.dataStore.data.map { it[MARKET_CAP_KEY] ?: 0.0 }
    val halvingProgress: Flow<Double> =
        context.dataStore.data.map { it[HALVING_PROGRESS_KEY] ?: 0.0 }
    val nextHalvingDate: Flow<String> =
        context.dataStore.data.map { it[NEXT_HALVING_DATE_KEY] ?: "" }
    val hashrate: Flow<String> = context.dataStore.data.map { it[HASHRATE_KEY] ?: "0 EH/s" }
    val quoteText: Flow<String> = context.dataStore.data.map { it[QUOTE_TEXT_KEY] ?: "" }
    val quoteSpeaker: Flow<String> = context.dataStore.data.map { it[QUOTE_SPEAKER_KEY] ?: "" }
    val quoteDate: Flow<String> = context.dataStore.data.map { it[QUOTE_DATE_KEY] ?: "" }
    
    // Cooldown state
    val isRefreshOnCooldown: Flow<Boolean> = context.dataStore.data.map { preferences ->
        val lastRefreshTime = preferences[LAST_REFRESH_TIME_KEY] ?: 0L
        val currentTime = System.currentTimeMillis()
        (currentTime - lastRefreshTime) < REFRESH_COOLDOWN_DURATION
    }

    // Refresh all data
    suspend fun refreshAllData() {
        // Check if refresh is on cooldown
        val preferences = context.dataStore.data.first()
        val lastRefreshTime = preferences[LAST_REFRESH_TIME_KEY] ?: 0L
        val currentTime = System.currentTimeMillis()
        if ((currentTime - lastRefreshTime) < REFRESH_COOLDOWN_DURATION) {
            return // Exit early if still on cooldown
        }
        
        try {
            // Update last refresh time at the start
            context.dataStore.edit { preferences ->
                preferences[LAST_REFRESH_TIME_KEY] = System.currentTimeMillis()
            }
            // Fetch data from APIs (one by one to handle partial failures)
            fetchWithRetry("USD price") { coinGeckoApi.getUSDPrice() }?.let { prices ->
                context.dataStore.edit { it[PRICE_USD_KEY] = prices.bitcoin.usd }
            }

            fetchWithRetry("latest block") { bitcoinApi.getLatestBlock() }?.let { blockTip ->
                context.dataStore.edit {
                    it[BLOCK_HEIGHT_KEY] = blockTip.height
                    it[HALVING_PROGRESS_KEY] = calculateHalvingProgress(blockTip.height)
                }
            }

            fetchWithRetry("supply") { bitcoinApi.getSupply() }?.let { supply ->
                context.dataStore.edit { it[SUPPLY_KEY] = supply.supply }
            }

            fetchWithRetry("mempool fees") { bitcoinApi.getMempoolFees() }?.let { fees ->
                context.dataStore.edit {
                    it[FEE_LOW_KEY] = fees.oneDay
                    it[FEE_MED_KEY] = fees.thirtyMin
                    it[FEE_HIGH_KEY] = fees.nextBlock.smart
                }
            }

            fetchWithRetry("market cap") { coinGeckoApi.getUSDPriceWithMarketCap() }?.let { marketCap ->
                context.dataStore.edit { it[MARKET_CAP_KEY] = marketCap.bitcoin.usd_market_cap }
            }

            fetchWithRetry("next halving") { bitcoinApi.getNextHalving() }?.let { halving ->
                context.dataStore.edit {
                    it[BLOCKS_TO_HALVING_KEY] = halving.blocksUntilNextHalving
                    it[NEXT_HALVING_DATE_KEY] = halving.nextHalvingEstimatedDate
                }
            }

            fetchWithRetry("hash rate") { bitcoinApi.getHashRate() }?.let { hashrate ->
                context.dataStore.edit {
                    it[HASHRATE_KEY] = String.format("%.2f %s/s", hashrate.oneDay.`val`, hashrate.oneDay.unitAbbreviation)
                }
            }

            fetchWithRetry("quote") { bitcoinApi.getQuote() }?.let { quote ->
                context.dataStore.edit {
                    it[QUOTE_TEXT_KEY] = quote.quote
                }
            }

            fetchWithRetry("THB price") { coinGeckoApi.getTHBPrice() }?.let { thbPrice ->
                context.dataStore.edit { it[PRICE_THB_KEY] = thbPrice.bitcoin.thb }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during data refresh", e)
        }
    }

    private suspend fun <T> fetchWithRetry(tag: String, block: suspend () -> T): T? {
        var delayMs = 1000L
        repeat(3) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                if (attempt == 2) {
                    Log.e(TAG, "Failed to fetch $tag after 3 attempts", e)
                } else {
                    Log.w(TAG, "Attempt ${attempt + 1} failed for $tag, retrying in ${delayMs}ms", e)
                    delay(delayMs)
                    delayMs *= 2
                }
            }
        }
        return null
    }

    private fun calculateHalvingProgress(currentHeight: Int): Double {
        val currentCycle = currentHeight / BLOCKS_PER_HALVING
        val cycleStart = currentCycle * BLOCKS_PER_HALVING
        val cycleProgress = currentHeight - cycleStart
        return (cycleProgress.toDouble() / BLOCKS_PER_HALVING.toDouble()) * 100.0
    }
}