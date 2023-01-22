package com.googof.bitcointimechainwidgets.widgets.mempool.blockheight

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.googof.bitcointimechainwidgets.widgets.mempool.Mempool
import com.googof.bitcointimechainwidgets.widgets.mempool.Mempool.Available
import com.googof.bitcointimechainwidgets.widgets.mempool.Mempool.Loading
import com.googof.bitcointimechainwidgets.widgets.mempool.Mempool.Unavailable
import com.googof.bitcointimechainwidgets.widgets.mempool.MempoolStateDefinition

class BlockHeightWidget : GlanceAppWidget() {

    // Override the state definition to use our custom one using Kotlin serialization
    override val stateDefinition = MempoolStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    @Composable
    fun Content() {
        val mempoolModel = currentState<Mempool>()
        GlanceTheme {
            when (mempoolModel) {
                Loading -> {
                    CircularProgressIndicator()
                }

                is Available ->
                        BlockHeightCompose(mempoolModel)

                is Unavailable -> {
                        Text("Data not available")
                        Button("Refresh", actionRunCallback<UpdateBlockHeightAction>())
                    }
            }
        }
    }
}

@Composable
fun BlockHeightCompose(mempoolModel: Available) {
    Column(
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = GlanceModifier.fillMaxSize()
            .clickable(actionRunCallback<UpdateBlockHeightAction>()),
    ) {

        Text(
            text = "Block Height",
            style = TextStyle(
                color = GlanceTheme.colors.primary,
            ),
            modifier = GlanceModifier.clickable(actionRunCallback<UpdateBlockHeightAction>())
        )

        Text(
            text = mempoolModel.blockHeight,
            style = TextStyle(
                color = GlanceTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            ),
            modifier = GlanceModifier.clickable(actionRunCallback<UpdateBlockHeightAction>())
        )
    }
}

/**
 * Force update the info after user click
 */
class UpdateBlockHeightAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Force the worker to refresh
//        BlockHeightWorker.enqueue(context = context, force = true)
    }
}
