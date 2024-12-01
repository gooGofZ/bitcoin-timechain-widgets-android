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

    @GET("price")
    suspend fun getPrice(): PriceResponse

    @GET("price/marketcap")
    suspend fun getMarketCap(): MarketCapResponse

    @GET("mempool/fees")
    suspend fun getMempoolFees(): MempoolFeesResponse

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