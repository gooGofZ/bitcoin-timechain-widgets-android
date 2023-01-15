package com.googof.bitcointimechainwidgets.mempool.blocks

import android.text.format.DateUtils
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.text.*
import androidx.compose.runtime.Composable
import androidx.glance.appwidget.GlanceAppWidget
import com.googof.bitcointimechainwidgets.AppWidgetColumn
import com.googof.bitcointimechainwidgets.GlanceTheme
import com.googof.bitcointimechainwidgets.util.getRelationTime
import java.util.*

class BlocksGlanceWidget : GlanceAppWidget() {

    @Composable
    override fun Content() {
        GlanceTheme {
            AppWidgetColumn {
                Blocks()
            }
        }
    }
}


@Composable
fun Blocks() {
    LazyColumn {
        item {
            Text(text = "Blocks")
        }
        item {
            Text(text = "Block 1")
        }
        item {
            Text(text = "Block 2")
        }
        item {
            Text(text = "Block 3")
        }
        item {
            Text(text = "Block 4")
        }
        item {
            Text(text = "Block 5")
        }
        item {
            Text(text = "Block 6")
        }
        item {
            Text(text = "Block 7")
        }
        item {
            Text(text = getRelationTime(Date().time))
        }
        item {
            Text(text = "Block 9")
        }
        item {
            Text(text = "Block 10")
        }
        item {
            Text(text = "Block 11")
        }
        item {
            Text(text = "Block 12")
        }
        item {
            Text(text = "Block 13")
        }
        item {
            Text(text = "Block 14")
        }
        item {
            Text(text = "Block 15")
        }

    }
}
