/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googof.bitcointimechainwidgets

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * MainActivity sets the content view activity_main, a fragment container that contains
 * overviewFragment.
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // App Version
        val textVersion = findViewById<TextView>(R.id.textVersion)
        textVersion.text = "Version : " + BuildConfig.VERSION_NAME

        // Donation
        // LNURL & Lightning Address
        var lnUrl =
            "LNURL1DP68GURN8GHJ7AMPD3KX2AR0VEEKZAR0WD5XJTNRDAKJ7TNHV4KXCTTTDEHHWM30D3H82UNVWQHHQMRPDEJKYETPW468JVP5VHYZW5"
        var lnAddress = "planebeauty04@walletofsatoshi.com"

        val textLNURL = findViewById<TextView>(R.id.textLNURL)
        textLNURL.text = lnUrl

        val textLightningAddress = findViewById<TextView>(R.id.textLightningAddress)
        textLightningAddress.text = lnAddress

        val btnLNURL = findViewById<Button>(R.id.btnLNURL)
        val btnLightningAddress = findViewById<Button>(R.id.btnLightningAddress)

        // set on-click listener
        btnLNURL.setOnClickListener {
            var myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            var myClip = ClipData.newPlainText("text", lnUrl)
            myClipboard.setPrimaryClip(myClip)

            // your code to perform when the user clicks on the button
            Toast.makeText(this@MainActivity, "Copied LNURL", Toast.LENGTH_SHORT).show()
        }

        // set on-click listener
        btnLightningAddress.setOnClickListener {
            var myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            var myClip = ClipData.newPlainText("text", lnAddress)
            myClipboard.setPrimaryClip(myClip)

            // your code to perform when the user clicks on the button
            Toast.makeText(this@MainActivity, "Copied Ligthning Address", Toast.LENGTH_SHORT).show()
        }

    }
}