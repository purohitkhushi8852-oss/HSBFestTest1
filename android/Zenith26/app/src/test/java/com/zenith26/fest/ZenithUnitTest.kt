package com.zenith26.fest

import com.zenith26.fest.data.api.ApiConfig
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.RegistrationIn
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ZenithUnitTest {

    @Test
    fun testEventAvailableSeatsCalculation() {
        val event = Event(
            id = 1,
            title = "Code Sprint 2.0",
            category = "Technical",
            description = "Fast-paced competitive coding contest.",
            eventDate = "2026-11-14",
            eventTime = "10:00 AM",
            venue = "Lab 3, Computing Block",
            capacity = 100,
            fee = 0,
            icon = "💻",
            availableSeats = 99
        )

        assertEquals(1, event.id)
        assertEquals("Code Sprint 2.0", event.title)
        assertEquals(99, event.availableSeats)
        assertTrue(event.availableSeats > 0)
        assertFalse(event.availableSeats == 0)
    }

    @Test
    fun testFullEventDetection() {
        val fullEvent = Event(
            id = 2,
            title = "Robo Race",
            category = "Technical",
            description = "Obstacle course for autonomous bots.",
            eventDate = "2026-11-14",
            eventTime = "02:00 PM",
            venue = "Open Amphitheatre",
            capacity = 30,
            fee = 150,
            icon = "🤖",
            availableSeats = 0
        )

        assertEquals(0, fullEvent.availableSeats)
    }

    @Test
    fun testRegistrationValidation() {
        val validRegistration = RegistrationIn(
            studentName = "Aarav Sharma",
            rollNumber = "2024BCSE042",
            email = "aarav.sharma@college.edu",
            phone = "+91 9876543210"
        )

        assertTrue(validRegistration.studentName.isNotBlank())
        assertTrue(validRegistration.rollNumber.isNotBlank())
        assertTrue(validRegistration.email.contains("@"))
        assertTrue(validRegistration.email.contains("."))
    }

    @Test
    fun testDefaultEmulatorUrl() {
        assertEquals("http://10.0.2.2:8081/", ApiConfig.DEFAULT_EMULATOR_URL)
        assertEquals("http://127.0.0.1:8081/", ApiConfig.DEFAULT_LOCALHOST_URL)
        assertEquals("http://172.16.52.51:8081/", ApiConfig.DEFAULT_HOST_WIFI_URL)
        assertEquals("http://172.16.52.51:8081/", ApiConfig.DEFAULT_DEVICE_LAN_URL)
        assertEquals("http://172.16.60.111:8081/", ApiConfig.DEFAULT_FALLBACK_LAN_URL)
    }

    @Test
    fun testLocalFestDataSourceSeedAndSearch() {
        com.zenith26.fest.data.repository.LocalFestDataSource.resetToInitialSeed()
        val allEvents = com.zenith26.fest.data.repository.LocalFestDataSource.getEvents(null, null)
        assertTrue(allEvents.size >= 14)

        val techEvents = com.zenith26.fest.data.repository.LocalFestDataSource.getEvents("Technical", null)
        assertTrue(techEvents.all { it.category == "Technical" })

        val codeEvents = com.zenith26.fest.data.repository.LocalFestDataSource.getEvents(null, "code")
        assertTrue(codeEvents.isNotEmpty())
    }

    @Test
    fun testLocalRegistrationAndSeatDecrement() {
        com.zenith26.fest.data.repository.LocalFestDataSource.resetToInitialSeed()
        val evBefore = com.zenith26.fest.data.repository.LocalFestDataSource.getEventDetail(1)!!
        val seatsBefore = evBefore.availableSeats

        val (success, _) = com.zenith26.fest.data.repository.LocalFestDataSource.registerForEvent(
            1,
            RegistrationIn("Local Student", "2024TEST999", "local.student@college.edu", "+91 9999999999")
        )
        assertTrue(success)

        val evAfter = com.zenith26.fest.data.repository.LocalFestDataSource.getEventDetail(1)!!
        assertEquals(seatsBefore - 1, evAfter.availableSeats)

        // Duplicate test
        val (dupSuccess, _) = com.zenith26.fest.data.repository.LocalFestDataSource.registerForEvent(
            1,
            RegistrationIn("Local Student", "2024TEST999", "local.student@college.edu", null)
        )
        assertFalse(dupSuccess)
    }
}
