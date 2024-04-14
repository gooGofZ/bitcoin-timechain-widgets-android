package com.googof.bitcointimechainwidgets.widgets.timechain


import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL_MEMPOOL = "https://mempool.space/api/"
private const val BASE_URL_BITNODES_IO = "https://bitnodes.io/"
private const val BASE_URL_BITCOIN_EXPLORER = "https://bitcoinexplorer.org/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL_MEMPOOL)
    .build()

private val retrofitBitnodes = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL_BITNODES_IO)
    .build()

private val retrofitBitcoinExplorer = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL_BITCOIN_EXPLORER)
    .build()

interface MempoolApiService {
    @GET("blocks/tip/height")
    suspend fun getBlockTipHeight(): String

    @GET("v1/fees/recommended")
    suspend fun getRecommendedFee(): Fees

    @GET("v1/mining/hashrate/3d")
    suspend fun getHashrate(): Hashrate

    @GET("mempool")
    suspend fun getUncomfirmedTX(): UnconfirmedTX

    @GET("v1/lightning/statistics/latest")
    suspend fun getLightningNetwork(): LightningNetwork

    //Last 15 mined blocks
    @GET("v1/blocks")
    suspend fun getLastestBlocks(): List<Block>
}

interface BitnodesApiService {
    @GET("api/v1/snapshots/?limit=1")
    suspend fun getSnapshots(): Nodes
}

interface BitcoinExplorerApiService {
    @GET("price/sats")
    suspend fun getMoscowTime(): MoscowTime

   @GET("blockchain/next-halving")
   suspend fun nextHalving(): NextHalving
}

object TimechainRepo {

    private val retrofitService: MempoolApiService by lazy {
        retrofit.create(MempoolApiService::class.java)
    }

    private val retrofitBitnodesService: BitnodesApiService by lazy {
        retrofitBitnodes.create(BitnodesApiService::class.java)
    }

    private val retrofitBitcoinExplorerService: BitcoinExplorerApiService by lazy {
        retrofitBitcoinExplorer.create(BitcoinExplorerApiService::class.java)
    }

    suspend fun getTimechainInfo(): TimechainInfo {
        val fees = retrofitService.getRecommendedFee()
        val blockHeight = retrofitService.getBlockTipHeight()
        val hashRate = retrofitService.getHashrate()
        val unconfirmedTX = retrofitService.getUncomfirmedTX()
        val nodes = retrofitBitnodesService.getSnapshots()
        val lightningNetwork = retrofitService.getLightningNetwork()
        val lastBlocks = retrofitService.getLastestBlocks()
        val moscowTime = retrofitBitcoinExplorerService.getMoscowTime()
        val nextHalving = retrofitBitcoinExplorerService.nextHalving()

        return TimechainInfo.Available(
            fastestFee = fees.fastestFee,
            halfHourFee = fees.halfHourFee,
            hourFee = fees.hourFee,
            economyFee = fees.economyFee,
            minimumFee = fees.minimumFee,
            currentHashrate = hashRate.currentHashrate,
            currentDifficulty = hashRate.currentDifficulty,
            count = unconfirmedTX.count,
            blockHeight = blockHeight,

            totalNode = nodes.count,

            blocks = lastBlocks,

            ln_channel_count = lightningNetwork.latest.channel_count,
            ln_node_count = lightningNetwork.latest.node_count,
            ln_total_capacity = lightningNetwork.latest.total_capacity / 100000000,

            moscowTime = moscowTime.usd,

            nextHalving = nextHalving
        )
    }
}