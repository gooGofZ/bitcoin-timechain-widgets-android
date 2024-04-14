package com.googof.bitcointimechainwidgets

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.googof.bitcointimechainwidgets.ui.theme.BitcoinTimechainWidgetsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BitcoinTimechainWidgetsTheme {
                Home()
            }

        }
    }
}

@Composable
fun Home(modifier: Modifier = Modifier) {
    // Context is required to show a toast message and to access the clipboard service
    val context = LocalContext.current
    val lnAddress = stringResource(R.string.app_ln_address)
    val lnUrl = stringResource(R.string.app_lnurl)

    // Attempting to get the app version name using PackageManager
    val appVersion = try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        " "
    }
    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        // App Version as Header
        Text(
            text = stringResource(R.string.app_name) + " " + appVersion,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Text(text = "How to use:", fontWeight = FontWeight.Bold)
        Text(text = "Choose the widget from your home screen widget picker.")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "How to update data:", fontWeight = FontWeight.Bold)
        Text(text = "Tap on the widget")
        Text(
            text = "The widgets automatic update every 15 minutes.",
            style = TextStyle(fontStyle = FontStyle.Italic)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Data providers:", fontWeight = FontWeight.Bold)
        Text(text = "- mempool.space")
        Text(text = "- bitnodes.io")
        Text(text = "- bitcoinexplorer.org")
        Text(text = "- satoshi.nakamotoinstitute.org")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Donation:", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))


        Text(text = "LN Address: $lnAddress")
        Spacer(modifier = Modifier.height(8.dp)) // Add some space between the buttons

        Text(text = "LN URL: $lnUrl")

        Spacer(modifier = Modifier.height(8.dp))

        Row {

            Button(modifier = Modifier.weight(1.0F), onClick = {
                // Showing a toast message
                Toast.makeText(context, "LN Address Copied! $lnAddress", Toast.LENGTH_SHORT).show()
                // Accessing the clipboard service
                val clipboardManager =
                    context.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
                val clipData = ClipData.newPlainText("text", lnAddress)
                clipboardManager?.setPrimaryClip(clipData)
            }) {
                Text("Copy LN Address")
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Button(modifier = Modifier.weight(1.0F), onClick = {
                // Showing a toast message for lnUrl
                Toast.makeText(context, "LNURL Copied! $lnUrl", Toast.LENGTH_SHORT).show()
                // Accessing the clipboard service for lnUrl
                val clipboardManager =
                    context.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
                val clipDataUrl = ClipData.newPlainText("text", lnUrl)
                clipboardManager?.setPrimaryClip(clipDataUrl)
            }) {
                Text("Copy LN URL")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Stay humble and stack sats",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}