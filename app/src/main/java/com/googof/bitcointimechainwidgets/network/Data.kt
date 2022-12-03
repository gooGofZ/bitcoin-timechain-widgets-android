package com.googof.bitcointimechainwidgets.network

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