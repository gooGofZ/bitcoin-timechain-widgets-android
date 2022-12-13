package com.googof.bitcointimechainwidgets.mempool

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

object MempoolInfoStateDefinition : GlanceStateDefinition<MempoolInfo> {
    private const val DATA_STORE_FILENAME = "mempoolInfo"
    private val Context.datastore by dataStore(DATA_STORE_FILENAME, MempoolInfoSerializer)

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<MempoolInfo> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }

    object MempoolInfoSerializer : Serializer<MempoolInfo> {
        override val defaultValue = MempoolInfo.Unavailable("no data found")

        override suspend fun readFrom(input: InputStream): MempoolInfo = try {
            Json.decodeFromString(
                MempoolInfo.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Could not read mempool data: ${exception.message}")
        }

        override suspend fun writeTo(t: MempoolInfo, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(MempoolInfo.serializer(), t).encodeToByteArray()
                )
            }
        }
    }
}