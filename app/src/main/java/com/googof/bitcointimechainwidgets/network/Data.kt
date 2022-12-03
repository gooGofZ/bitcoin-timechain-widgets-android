package com.googof.bitcointimechainwidgets.network

//Mempool Data
data class Fees(
    //{
    //    fastestFee: 1,
    //    halfHourFee: 1,
    //    hourFee: 1,
    //    economyFee: 1,
    //    minimumFee: 1
    //}

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

//Bitnodes.io
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

data class Quote(
    val text: String,
    val speaker: String,
    val date: String,
)