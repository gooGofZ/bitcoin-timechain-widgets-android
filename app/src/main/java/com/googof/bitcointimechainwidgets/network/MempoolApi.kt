package com.googof.bitcointimechainwidgets.network

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// MempoolApi.kt
interface MempoolApi {
    @GET("blocks/tip/height")
    suspend fun getBlockHeight(): Int

    @GET("v1/prices")
    suspend fun getPrices(): PricesResponse

    companion object {
        fun create(): MempoolApi {
            return Retrofit.Builder()
                .baseUrl("https://mempool.space/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MempoolApi::class.java)
        }
    }
}

data class PricesResponse(
    val time: Long,
    val USD: Int,
    val EUR: Int,
    val GBP: Int,
    val CAD: Int,
    val CHF: Int,
    val AUD: Int,
    val JPY: Int
)