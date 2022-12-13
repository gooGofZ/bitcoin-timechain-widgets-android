package com.googof.bitcointimechainwidgets.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL =
    "https://bitcoinexplorer.org/api/" //

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface BitcoinExplorerApiService {
    @GET("quotes/random") // Get snapshots latest one.
    suspend fun getRandomQuote(): Quote
}

object BitcoinExplorerApi {
    val retrofitService : BitcoinExplorerApiService by lazy {
        retrofit.create(BitcoinExplorerApiService::class.java) }
}
