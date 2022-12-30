package com.googof.bitcointimechainwidgets.satoshiquotes

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

object SatoshiQuoteStateDefinition : GlanceStateDefinition<SatoshiQuoteInfo> {

    private const val DATA_STORE_FILENAME = "satoshiquote"
    private val Context.datastore by dataStore(DATA_STORE_FILENAME, SatoshiQuoteStateDefinition)

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<SatoshiQuoteInfo> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }

    object SatoshiQuoteStateDefinition : Serializer<SatoshiQuoteInfo> {
        override val defaultValue = SatoshiQuoteInfo.Unavailable("no data found")

        override suspend fun readFrom(input: InputStream): SatoshiQuoteInfo = try {
            Json.decodeFromString(
                SatoshiQuoteInfo.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Could not read mempool data: ${exception.message}")
        }

        override suspend fun writeTo(t: SatoshiQuoteInfo, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(SatoshiQuoteInfo.serializer(), t).encodeToByteArray()
                )
            }
        }
    }
}