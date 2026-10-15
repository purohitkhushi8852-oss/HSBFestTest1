package com.zenith26.fest.data.repository

import android.util.Log
import com.zenith26.fest.data.api.ApiClient
import com.zenith26.fest.data.api.ApiConfig
import com.zenith26.fest.data.model.CancelResponse
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.EventCreate
import com.zenith26.fest.data.model.RegistrationIn
import com.zenith26.fest.data.model.RegistrationItem
import com.zenith26.fest.data.model.RegistrationOut
import com.zenith26.fest.data.model.Stats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class ZenithRepository {

    private val tag = "ZenithRepository"

    /**
     * Attempts to find a working live endpoint from candidates,
     * or returns null if all live network endpoints are unreachable.
     */
    private suspend fun tryFindWorkingEndpoint(): Boolean {
        val candidates = ApiConfig.getCandidateUrls()
        for (url in candidates) {
            try {
                ApiConfig.setBaseUrl(url)
                val service = ApiClient.getService()
                val res = service.getEvents(null, null)
                if (res.isSuccessful) {
                    ApiConfig.isLiveConnected = true
                    ApiConfig.activeServerLabel = "Live: $url"
                    Log.d(tag, "Successfully connected to live backend at $url")
                    return true
                }
            } catch (e: Exception) {
                Log.d(tag, "Endpoint $url unreachable: ${e.message}")
            }
        }
        ApiConfig.isLiveConnected = false
        ApiConfig.activeServerLabel = "Offline / Standalone Mode"
        return false
    }

    suspend fun getEvents(category: String? = null, search: String? = null): Result<List<Event>> =
        withContext(Dispatchers.IO) {
            val catFilter = if (category.equals("All", ignoreCase = true)) null else category
            val searchFilter = if (search.isNullOrBlank()) null else search

            try {
                val service = ApiClient.getService()
                val response = service.getEvents(category = catFilter, search = searchFilter)
                if (response.isSuccessful && response.body() != null) {
                    ApiConfig.isLiveConnected = true
                    ApiConfig.activeServerLabel = "Live: ${ApiConfig.getBaseUrl()}"
                    return@withContext Result.success(response.body()!!)
                }
            } catch (e: IOException) {
                Log.w(tag, "Primary URL ${ApiConfig.getBaseUrl()} failed: ${e.message}. Attempting auto-discovery...")
                if (tryFindWorkingEndpoint()) {
                    try {
                        val service = ApiClient.getService()
                        val response = service.getEvents(category = catFilter, search = searchFilter)
                        if (response.isSuccessful && response.body() != null) {
                            return@withContext Result.success(response.body()!!)
                        }
                    } catch (ignored: Exception) {}
                }
            } catch (e: Exception) {
                Log.e(tag, "Non-network error fetching events: ${e.message}")
            }

            // Seamless fallback to built-in local database engine
            Log.i(tag, "Serving events from built-in local database engine (100% functional offline)")
            Result.success(LocalFestDataSource.getEvents(catFilter, searchFilter))
        }

    suspend fun getEventDetail(eventId: Int): Result<Event> =
        withContext(Dispatchers.IO) {
            try {
                val service = ApiClient.getService()
                val response = service.getEventDetail(eventId)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext Result.success(response.body()!!)
                }
            } catch (e: Exception) {
                Log.w(tag, "Live detail failed, using local source: ${e.message}")
            }

            val localEvent = LocalFestDataSource.getEventDetail(eventId)
            if (localEvent != null) {
                Result.success(localEvent)
            } else {
                Result.failure(Exception("Event with id $eventId not found"))
            }
        }

    suspend fun registerForEvent(eventId: Int, payload: RegistrationIn): Result<RegistrationOut> =
        withContext(Dispatchers.IO) {
            try {
                val service = ApiClient.getService()
                val response = service.registerForEvent(eventId, payload)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext Result.success(response.body()!!)
                } else {
                    val errorMsg = ApiClient.parseErrorMessage(response)
                    return@withContext Result.failure(Exception(errorMsg))
                }
            } catch (e: IOException) {
                Log.w(tag, "Live registration failed, submitting to local engine: ${e.message}")
            } catch (e: Exception) {
                Log.e(tag, "Unexpected error: ${e.message}")
            }

            // Local registration execution with full validation & seat decrement
            val (success, result) = LocalFestDataSource.registerForEvent(eventId, payload)
            if (success && result is RegistrationOut) {
                Result.success(result)
            } else {
                Result.failure(Exception(result.toString()))
            }
        }

    suspend fun getRegistrations(eventId: Int? = null): Result<List<RegistrationItem>> =
        withContext(Dispatchers.IO) {
            try {
                val service = ApiClient.getService()
                val response = service.getRegistrations(eventId)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext Result.success(response.body()!!)
                }
            } catch (e: Exception) {
                Log.w(tag, "Live registrations failed, using local source: ${e.message}")
            }
            Result.success(LocalFestDataSource.getRegistrations(eventId))
        }

    suspend fun getStats(): Result<Stats> =
        withContext(Dispatchers.IO) {
            try {
                val service = ApiClient.getService()
                val response = service.getStats()
                if (response.isSuccessful && response.body() != null) {
                    return@withContext Result.success(response.body()!!)
                }
            } catch (e: Exception) {
                Log.w(tag, "Live stats failed, using local source: ${e.message}")
            }
            Result.success(LocalFestDataSource.getStats())
        }

    suspend fun createEvent(payload: EventCreate): Result<Event> =
        withContext(Dispatchers.IO) {
            try {
                val service = ApiClient.getService()
                val response = service.createEvent(payload)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext Result.success(response.body()!!)
                } else {
                    val errorMsg = ApiClient.parseErrorMessage(response)
                    return@withContext Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Log.w(tag, "Live create event failed, using local source: ${e.message}")
            }
            Result.success(LocalFestDataSource.createEvent(payload))
        }

    suspend fun cancelRegistration(registrationId: Int): Result<CancelResponse> =
        withContext(Dispatchers.IO) {
            try {
                val service = ApiClient.getService()
                val response = service.cancelRegistration(registrationId)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext Result.success(response.body()!!)
                } else {
                    val errorMsg = ApiClient.parseErrorMessage(response)
                    return@withContext Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Log.w(tag, "Live cancel failed, using local source: ${e.message}")
            }

            val (success, msg) = LocalFestDataSource.cancelRegistration(registrationId)
            if (success) {
                Result.success(CancelResponse(detail = msg, id = registrationId))
            } else {
                Result.failure(Exception(msg))
            }
        }
}
