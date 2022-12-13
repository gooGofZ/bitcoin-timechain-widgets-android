package com.googof.bitcointimechainwidgets.mempool

import kotlinx.serialization.Serializable

@Serializable
sealed interface MempoolInfo {
    @Serializable
    object Loading : MempoolInfo

    @Serializable
    data class Available(
        val fastestFee: String,
        val halfHourFee: String,
        val hourFee: String,
        val economyFee: String,
        val minimumFee: String,
        val currentHashrate: Double,
        val currentDifficulty: Double,
        val count: Int,
        val blockHeight: String,
        val totalNode: Int,
    ) : MempoolInfo

    @Serializable
    data class Unavailable(val message: String) : MempoolInfo
}

//Mempool Data
data class Fees(

    val fastestFee: String,
    val halfHourFee: String,
    val hourFee: String,
    val economyFee: String,
    val minimumFee: String
)

data class Hashrate(
    val currentHashrate: Double,
    val currentDifficulty: Double
)

data class UnconfirmedTX(
    val count: Int
)


//Bitnodes.io Data
data class Snapshot(
    val url: String,
    val timestamp: Int,
    val total_nodes: Int,
    val latest_height: Int,
)

data class Nodes(
    val count: Int,
    val next: String,
    val previous: String?,
    val results: List<Snapshot>,
)