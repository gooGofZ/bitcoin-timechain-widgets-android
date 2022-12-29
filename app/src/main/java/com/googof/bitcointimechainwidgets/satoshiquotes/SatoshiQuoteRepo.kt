package com.googof.bitcointimechainwidgets.satoshiquotes

import com.googof.bitcointimechainwidgets.quotes.QuoteApiService
import com.googof.bitcointimechainwidgets.quotes.QuoteInfo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import kotlin.random.Random

private const val BASE_URL = "https://raw.githubusercontent.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface SatoshiQuoteApiService {
    @GET("gooGofZ/gooGofz.github.io/main/satoshi_quotes.json")
    suspend fun getSatoshiQuotes(): List<SatoshiQuote>
}

object SatoshiQuoteRepo {
    private val retrofitService: SatoshiQuoteApiService by lazy {
        retrofit.create(SatoshiQuoteApiService::class.java)
    }

    suspend fun getRandomSatoshiQuote(): SatoshiQuoteInfo {
        val quotes = retrofitService.getSatoshiQuotes()

        println("quotes.size")
        println(quotes.size)

        val randomQuote = (quotes[Random.nextInt(0, quotes.size - 1)])
        return SatoshiQuoteInfo.Available(
            date = randomQuote.date,
            text = randomQuote.text,
            category = randomQuote.category,
            medium = randomQuote.medium
        )
    }
}

