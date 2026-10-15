package com.zenith26.fest.data.api

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.zenith26.fest.BuildConfig

object ApiConfig {
    private const val PREFS_NAME = "zenith26_api_prefs"
    private const val KEY_BASE_URL = "base_url"

    // Presets & Fallbacks for Emulators & Physical Devices
    const val DEFAULT_EMULATOR_URL = "http://10.0.2.2:8081/"
    const val DEFAULT_LOCALHOST_URL = "http://127.0.0.1:8081/"
    const val DEFAULT_HOST_WIFI_URL = "http://172.16.52.51:8081/"
    const val DEFAULT_DEVICE_LAN_URL = "http://172.16.52.51:8081/"
    const val DEFAULT_FALLBACK_LAN_URL = "http://172.16.60.111:8081/"

    private var prefs: SharedPreferences? = null
    var onUrlChangedListener: ((String) -> Unit)? = null

    // Live connection tracking
    var isLiveConnected: Boolean = false
    var activeServerLabel: String = "Connecting..."

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isEmulator(): Boolean {
        val fp = Build.FINGERPRINT ?: ""
        val model = Build.MODEL ?: ""
        val manufacturer = Build.MANUFACTURER ?: ""
        val brand = Build.BRAND ?: ""
        val device = Build.DEVICE ?: ""
        val product = Build.PRODUCT ?: ""

        return fp.startsWith("generic") ||
                fp.startsWith("unknown") ||
                model.contains("google_sdk", ignoreCase = true) ||
                model.contains("Emulator", ignoreCase = true) ||
                model.contains("Android SDK built for x86", ignoreCase = true) ||
                manufacturer.contains("Genymotion", ignoreCase = true) ||
                (brand.startsWith("generic") && device.startsWith("generic")) ||
                "google_sdk" == product
    }

    /**
     * Resolves the starting base URL:
     * 1. Reads BuildConfig.BASE_URL (configured via ZENITH_API_BASE_URL env var during build)
     * 2. If running on emulator: defaults to 10.0.2.2:8081
     * 3. If running on physical device: defaults to host Wi-Fi IP (172.16.52.51:8081)
     */
    fun getInitialDefaultUrl(): String {
        return try {
            val buildUrl = BuildConfig.BASE_URL
            if (isEmulator()) {
                DEFAULT_EMULATOR_URL
            } else if (buildUrl.isNotBlank()) {
                if (buildUrl.endsWith("/")) buildUrl else "$buildUrl/"
            } else {
                DEFAULT_HOST_WIFI_URL
            }
        } catch (_: Exception) {
            if (isEmulator()) DEFAULT_EMULATOR_URL else DEFAULT_HOST_WIFI_URL
        }
    }

    fun getBaseUrl(): String {
        val stored = prefs?.getString(KEY_BASE_URL, null)
        val url = if (stored.isNullOrBlank()) getInitialDefaultUrl() else stored
        return if (url.endsWith("/")) url else "$url/"
    }

    fun setBaseUrl(newUrl: String) {
        val cleanUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        prefs?.edit()?.putString(KEY_BASE_URL, cleanUrl)?.apply()
        onUrlChangedListener?.invoke(cleanUrl)
    }

    fun getCandidateUrls(): List<String> {
        val active = getBaseUrl()
        val candidates = linkedSetOf(
            active,
            DEFAULT_HOST_WIFI_URL,
            "http://172.16.52.51:8000/",
            DEFAULT_FALLBACK_LAN_URL,
            "http://172.16.60.111:8000/",
            DEFAULT_EMULATOR_URL,
            "http://10.0.2.2:8000/",
            DEFAULT_LOCALHOST_URL
        )
        return candidates.toList()
    }

    fun resetToDefault() {
        setBaseUrl(getInitialDefaultUrl())
    }
}
