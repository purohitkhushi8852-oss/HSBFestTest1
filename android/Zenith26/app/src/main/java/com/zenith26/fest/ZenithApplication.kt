package com.zenith26.fest

import android.app.Application
import com.zenith26.fest.data.api.ApiConfig

class ZenithApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiConfig.init(this)
    }
}
