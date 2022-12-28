package com.googof.bitcointimechainwidgets.quotes

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://bitcoinexplorer.org/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface QuoteApiService {
    @GET("quotes/random")
    suspend fun getRandomQuote(): Quote
}

object QuoteRepo {

    private val retrofitService: QuoteApiService by lazy {
        retrofit.create(QuoteApiService::class.java)
    }

    suspend fun getQuoteInfo(): QuoteInfo {
        val quote = retrofitService.getRandomQuote()

        return QuoteInfo.Available(
            date = quote.date,
            speaker = quote.speaker,
            text = quote.text
        )
    }
}
