package com.googof.bitcointimechainwidgets

import android.app.Application
import androidx.work.Configuration

class App : Application(), Configuration.Provider {
    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder().build()
}
