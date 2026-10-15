package com.zenith26.fest.data.api

import com.google.gson.Gson
import com.zenith26.fest.data.model.ApiErrorResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val gson = Gson()
    private var currentBaseUrl: String = ApiConfig.getBaseUrl()
    private var currentService: ZenithApiService? = null

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    init {
        ApiConfig.onUrlChangedListener = { newUrl ->
            if (newUrl != currentBaseUrl) {
                currentBaseUrl = newUrl
                currentService = buildService(newUrl)
            }
        }
    }

    private fun buildService(baseUrl: String): ZenithApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(ZenithApiService::class.java)
    }

    fun getService(): ZenithApiService {
        val activeUrl = ApiConfig.getBaseUrl()
        if (currentService == null || activeUrl != currentBaseUrl) {
            currentBaseUrl = activeUrl
            currentService = buildService(activeUrl)
        }
        return currentService!!
    }

    /**
     * Extracts exact error message from FastAPI HTTPException response:
     * e.g. {"detail": "Already registered for this event"} -> "Already registered for this event"
     */
    fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val parsed = gson.fromJson(errorBody, ApiErrorResponse::class.java)
                parsed.detail ?: errorBody
            } else {
                "Server returned HTTP ${response.code()} ${response.message()}"
            }
        } catch (e: Exception) {
            "HTTP ${response.code()} ${response.message()}"
        }
    }
}
