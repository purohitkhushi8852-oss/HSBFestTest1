package com.zenith26.fest.data.api

import com.zenith26.fest.data.model.CancelResponse
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.EventCreate
import com.zenith26.fest.data.model.RegistrationIn
import com.zenith26.fest.data.model.RegistrationItem
import com.zenith26.fest.data.model.RegistrationOut
import com.zenith26.fest.data.model.Stats
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ZenithApiService {

    @GET("api/events")
    suspend fun getEvents(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): Response<List<Event>>

    @GET("api/events/{event_id}")
    suspend fun getEventDetail(
        @Path("event_id") eventId: Int
    ): Response<Event>

    @POST("api/events/{event_id}/register")
    suspend fun registerForEvent(
        @Path("event_id") eventId: Int,
        @Body payload: RegistrationIn
    ): Response<RegistrationOut>

    @GET("api/registrations")
    suspend fun getRegistrations(
        @Query("event_id") eventId: Int? = null
    ): Response<List<RegistrationItem>>

    @GET("api/stats")
    suspend fun getStats(): Response<Stats>

    // Stretch Goals
    @POST("api/events")
    suspend fun createEvent(
        @Body payload: EventCreate
    ): Response<Event>

    @DELETE("api/registrations/{registration_id}")
    suspend fun cancelRegistration(
        @Path("registration_id") registrationId: Int
    ): Response<CancelResponse>
}
