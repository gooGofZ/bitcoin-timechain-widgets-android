package com.googof.bitcointimechainwidgets.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.googof.bitcointimechainwidgets.network.BitcoinExplorerApi
import com.googof.bitcointimechainwidgets.network.BitnodesApi
import com.googof.bitcointimechainwidgets.network.CoinGeckoApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.edit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bitcoin_data")

class BitcoinDataRepository(private val context: Context) {

    // DataStore keys
    companion object {
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
        val TOTAL_NODES_KEY = intPreferencesKey("total_nodes")
        val QUOTE_TEXT_KEY = stringPreferencesKey("quote_text")
        val QUOTE_SPEAKER_KEY = stringPreferencesKey("quote_speaker")
        val QUOTE_DATE_KEY = stringPreferencesKey("quote_date")
    }

    // Network APIs
    private val bitcoinApi = BitcoinExplorerApi.create()
    private val bitnodesApi = BitnodesApi.create()
    private val coinGeckoApi = CoinGeckoApi.create()

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
    val totalNodes: Flow<Int> = context.dataStore.data.map { it[TOTAL_NODES_KEY] ?: 0 }
    val quoteText: Flow<String> = context.dataStore.data.map { it[QUOTE_TEXT_KEY] ?: "" }
    val quoteSpeaker: Flow<String> = context.dataStore.data.map { it[QUOTE_SPEAKER_KEY] ?: "" }
    val quoteDate: Flow<String> = context.dataStore.data.map { it[QUOTE_DATE_KEY] ?: "" }

    // Refresh all data
    suspend fun refreshAllData() {
        try {
            // Fetch data from APIs (one by one to handle partial failures)
            try {
                val prices = bitcoinApi.getPrice()
                context.dataStore.edit { preferences ->
                    preferences[PRICE_USD_KEY] = prices.usd.toDouble()
                }
            } catch (e: Exception) {
                // Continue with other APIs
            }

            try {
                val blockTip = bitcoinApi.getLatestBlock()
                context.dataStore.edit { preferences ->
                    preferences[BLOCK_HEIGHT_KEY] = blockTip.height
                    preferences[HALVING_PROGRESS_KEY] = calculateHalvingProgress(blockTip.height)
                }
            } catch (e: Exception) {
                // Continue with other APIs
            }

            try {
                val supply = bitcoinApi.getSupply()
                context.dataStore.edit { preferences ->
                    preferences[SUPPLY_KEY] = supply.supply
                }
            } catch (e: Exception) {
                // Continue with other APIs
            }

            try {
                val fees = bitcoinApi.getMempoolFees()
                context.dataStore.edit { preferences ->
                    preferences[FEE_LOW_KEY] = fees.oneDay
                    preferences[FEE_MED_KEY] = fees.thirtyMin
                    preferences[FEE_HIGH_KEY] = fees.nextBlock.smart
                }
            } catch (e: Exception) {
                // Continue with other APIs
            }

            try {
                val marketCap = bitcoinApi.getMarketCap()
                context.dataStore.edit { preferences ->
                    preferences[MARKET_CAP_KEY] = marketCap.usd
                }
            } catch (e: Exception) {
                // Continue with other APIs
            }

            try {
                val halving = bitcoinApi.getNextHalving()
                context.dataStore.edit { preferences ->
                    preferences[BLOCKS_TO_HALVING_KEY] = halving.blocksUntilNextHalving
                    preferences[NEXT_HALVING_DATE_KEY] = halving.nextHalvingEstimatedDate
                }
            } catch (e: Exception) {
                // Continue with other APIs
            }

            try {
                val hashrate = bitcoinApi.getHashRate()
                context.dataStore.edit { preferences ->
                    preferences[HASHRATE_KEY] = hashrate.`1Day`.string1
                }
            } catch (e: Exception) {
                // Continue with other APIs
            }

            try {
                val quote = bitcoinApi.getQuote()
                context.dataStore.edit { preferences ->
                    preferences[QUOTE_TEXT_KEY] = quote.text
                    preferences[QUOTE_SPEAKER_KEY] = quote.speaker
                    preferences[QUOTE_DATE_KEY] = quote.date
                }
            } catch (e: Exception) {
                // Continue with other APIs
            }

            try {
                val thbPrice = coinGeckoApi.getTHBPrice()
                context.dataStore.edit { preferences ->
                    preferences[PRICE_THB_KEY] = thbPrice.bitcoin.thb
                }
            } catch (e: Exception) {
                // Continue with other APIs
            }

            try {
                val nodes = bitnodesApi.getSnapshots()
                context.dataStore.edit { preferences ->
                    preferences[TOTAL_NODES_KEY] = nodes.results.firstOrNull()?.total_nodes ?: 0
                }
            } catch (e: Exception) {
                // Continue with other APIs
            }

        } catch (e: Exception) {
            // Overall error handling
        }
    }

    private fun calculateHalvingProgress(currentHeight: Int): Double {
        val blocksPerHalving = 210000
        val currentCycle = currentHeight / blocksPerHalving
        val cycleStart = currentCycle * blocksPerHalving
        val cycleProgress = currentHeight - cycleStart
        return (cycleProgress.toDouble() / blocksPerHalving.toDouble()) * 100.0
    }
}