package com.googof.bitcointimechainwidgets.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL =
    "https://bitnodes.io/api/" // Requests originating from the same IP address is limited to a maximum of 5000 requests per day.

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface BitnodesApiService {
    @GET("v1/snapshots/?limit=1") // Get snapshots latest one.
    suspend fun getTotalNodes(): Nodes
}

object BitnodesApi {
    val retrofitService : BitnodesApiService by lazy {
        retrofit.create(BitnodesApiService::class.java) }
}
