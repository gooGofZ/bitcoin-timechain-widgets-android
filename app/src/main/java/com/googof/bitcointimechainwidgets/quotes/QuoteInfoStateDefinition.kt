package com.googof.bitcointimechainwidgets.quotes

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object QuoteInfoStateDefinition : GlanceStateDefinition<QuoteInfo> {

    private const val DATA_STORE_FILENAME = "quoteInfo"
    private val Context.datastore by dataStore(DATA_STORE_FILENAME, QuoteInfoStateDefinition)

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<QuoteInfo> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }

    object QuoteInfoStateDefinition : Serializer<QuoteInfo> {
        override val defaultValue = QuoteInfo.Unavailable("no data found")

        override suspend fun readFrom(input: InputStream): QuoteInfo = try {
            Json.decodeFromString(
                QuoteInfo.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Could not read mempool data: ${exception.message}")
        }

        override suspend fun writeTo(t: QuoteInfo, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(QuoteInfo.serializer(), t).encodeToByteArray()
                )
            }
        }
    }
}