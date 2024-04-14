package com.googof.bitcointimechainwidgets.widgets.timechain

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

object TimechainStateDefinition :
    GlanceStateDefinition<TimechainInfo> {
    private const val DATA_STORE_FILENAME = "timechain"
    private val Context.datastore by dataStore(DATA_STORE_FILENAME, MempoolModelSerializer)

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<TimechainInfo> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }

    object MempoolModelSerializer : Serializer<TimechainInfo> {
        override val defaultValue = TimechainInfo.Unavailable("no data found")

        override suspend fun readFrom(input: InputStream): TimechainInfo = try {
            Json.decodeFromString(
                TimechainInfo.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Could not read mempool data: ${exception.message}")
        }

        override suspend fun writeTo(t: TimechainInfo, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(TimechainInfo.serializer(), t).encodeToByteArray()
                )
            }
        }
    }
}