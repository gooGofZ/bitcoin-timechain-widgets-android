package com.googof.bitcointimechainwidgets.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL =
    "https://mempool.space/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface MempoolApiService {
    @GET("blocks/tip/height")
    //suspend fun getBlockTipHeight(): String
    suspend fun getBlockTipHeight(): String

    @GET("v1/fees/recommended")
    //suspend fun getRecommendedFee(): Fees
    suspend fun getRecommendedFee(): Fees

    @GET("v1/mining/hashrate/3d")
    suspend fun getHashrate(): Hashrate

    @GET("mempool")
    suspend fun getUncomfirmedTX(): UnconfirmedTX
}

object MempoolApi {
    val retrofitService : MempoolApiService by lazy {
        retrofit.create(MempoolApiService::class.java) }
}
