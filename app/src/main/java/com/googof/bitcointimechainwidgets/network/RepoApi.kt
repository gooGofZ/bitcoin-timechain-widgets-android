package com.googof.bitcointimechainwidgets.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface BitcoinExplorerApi {
    @GET("blocks/tip")
    suspend fun getLatestBlock(): BlockTipResponse

    @GET("blockchain/coins")
    suspend fun getSupply(): SupplyResponse

    @GET("mempool/fees")
    suspend fun getMempoolFees(): MempoolFeesResponse

    @GET("blockchain/next-halving")
    suspend fun getNextHalving(): NextHalvingResponse

    @GET("mining/hashrate")
    suspend fun getHashRate(): HashRateStats

    @GET("quotes/random/")
    suspend fun getQuote(): Quote


    companion object {
        fun create(): BitcoinExplorerApi {
            return Retrofit.Builder()
                .baseUrl("https://api.frozenfork.cc/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BitcoinExplorerApi::class.java)
        }
    }
}

interface BitnodesApi {
    @GET("snapshots/")
    suspend fun getSnapshots(): SnapshotResponse

    companion object {
        private const val BASE_URL = "https://bitnodes.io/api/v1/"

        fun create(): BitnodesApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BitnodesApi::class.java)
        }
    }
}

interface CoinGeckoApi {
    @GET("simple/price?ids=bitcoin&vs_currencies=thb")
    suspend fun getTHBPrice(): BitcoinPriceResponse

    @GET("simple/price?ids=bitcoin&vs_currencies=usd")
    suspend fun getUSDPrice(): BitcoinUSDPriceResponse

    @GET("simple/price?ids=bitcoin&vs_currencies=usd&include_market_cap=true")
    suspend fun getUSDPriceWithMarketCap(): BitcoinUSDPriceWithMarketCapResponse

    companion object {
        private const val BASE_URL = "https://api.coingecko.com/api/v3/"

        fun create(): CoinGeckoApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CoinGeckoApi::class.java)
        }
    }
}

// Data Models

class PriceResponse(
    val usd: String,
    val eur: String,
    val gbp: String
)

data class BlockTipResponse(
    val height: Int,
    val hash: String
)

data class MarketCapResponse(
    val usd: Double,
    val eur: Double,
    val gbp: Double,
    val xau: Double
)

data class SupplyResponse(
    val supply: String,
    val type: String
)

data class NextBlockFees(
    val smart: Int,
    val min: Int,
    val max: Int,
    val median: Int
)

data class MempoolFeesResponse(
    val nextBlock: NextBlockFees,
    @SerializedName("30min") val thirtyMin: Int,
    @SerializedName("60min") val sixtyMin: Int,
    @SerializedName("1day") val oneDay: Int
)

data class NextHalvingResponse(
    val nextHalvingIndex: Int,
    val nextHalvingBlock: Int,
    val nextHalvingSubsidy: String,
    val blocksUntilNextHalving: Int,
    val timeUntilNextHalving: String,
    val nextHalvingEstimatedDate: String
)

data class HashRateStats(
    @SerializedName("1Day") val oneDay: HashRateData,
    @SerializedName("7Day") val sevenDay: HashRateData,
    @SerializedName("30Day") val thirtyDay: HashRateData,
    @SerializedName("90day") val ninetyDay: HashRateData,
    @SerializedName("365Day") val threeSixtyFiveDay: HashRateData
)

data class HashRateData(
    val `val`: Double,
    val unit: String,
    val unitAbbreviation: String,
    val unitExponent: String,
    val unitMultiplier: String,
    val raw: String,
    val string1: String,
    val string2: String,
    val string3: String
)

data class SnapshotResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Snapshot>
)

data class Snapshot(
    val url: String,
    val timestamp: Long,
    val total_nodes: Int,
    val latest_height: Int
)

data class Quote(
    val text: String,
    val speaker: String,
    val date: String,
    val url: String,
    val quoteIndex: Int
)

data class BitcoinPrice(
    val thb: Double
)

data class BitcoinPriceResponse(
    val bitcoin: BitcoinPrice
)

data class BitcoinUSDPrice(
    val usd: Double
)

data class BitcoinUSDPriceResponse(
    val bitcoin: BitcoinUSDPrice
)

data class BitcoinUSDPriceWithMarketCap(
    val usd: Double,
    val usd_market_cap: Double
)

data class BitcoinUSDPriceWithMarketCapResponse(
    val bitcoin: BitcoinUSDPriceWithMarketCap
)
