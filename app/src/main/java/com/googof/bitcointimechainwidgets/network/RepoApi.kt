package com.googof.bitcointimechainwidgets.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.math.BigInteger

interface BitcoinExplorerApi {
    @GET("blocks/tip")
    suspend fun getLatestBlock(): BlockTipResponse

    @GET("blockchain/coins")
    suspend fun getSupply(): SupplyResponse

    @GET("price")
    suspend fun getPrice(): PriceResponse

    @GET("price/marketcap")
    suspend fun getMarketCap(): MarketCapResponse

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
                .baseUrl("https://bitcoinexplorer.org/api/")
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


// Data Models
data class PriceResponse(
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
    val `1Day`: HashRateData,
    val `7Day`: HashRateData,
    val `30Day`: HashRateData,
    val `90day`: HashRateData,
    val `365Day`: HashRateData
)

data class HashRateData(
    val `val`: Double,
    val unit: String,
    val unitAbbreviation: String,
    val unitExponent: String,
    val unitMultiplier: BigInteger,
    val raw: BigInteger,
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