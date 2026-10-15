package com.zenith26.fest.data.repository

import com.zenith26.fest.data.model.CancelResponse
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.EventCreate
import com.zenith26.fest.data.model.RegistrationIn
import com.zenith26.fest.data.model.RegistrationItem
import com.zenith26.fest.data.model.RegistrationOut
import com.zenith26.fest.data.model.Stats
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Built-in local data source ensuring the mobile app is 100% functional
 * on physical Android devices even when offline or disconnected from host PC.
 */
object LocalFestDataSource {

    private val events = mutableListOf<Event>()
    private val registrations = mutableListOf<RegistrationItem>()
    private var nextRegId = 1
    private var nextEventId = 15

    init {
        resetToInitialSeed()
    }

    fun resetToInitialSeed() {
        events.clear()
        registrations.clear()
        nextRegId = 1

        val initialList = listOf(
            Event(1, "Code Sprint 2.0", "Technical", "Fast-paced competitive coding contest. 3 rounds of algorithmic problem solving.", "2026-11-14", "10:00 AM", "Lab 3, Computing Block", 100, 0, "💻", 99),
            Event(2, "Robo Race", "Technical", "Obstacle course for autonomous and RC bots. Time-trial format with elimination rounds.", "2026-11-14", "02:00 PM", "Open Amphitheatre", 30, 150, "🤖", 28),
            Event(3, "Battle of the Bands", "Cultural", "College rock and fusion band competition. 20-minute live set per band.", "2026-11-14", "06:00 PM", "Main Stage, Central Grounds", 500, 0, "🎸", 498),
            Event(4, "Street Dance Battle", "Cultural", "1v1 and crew-vs-crew street dance face-off. Breaking, popping, and hip-hop.", "2026-11-15", "04:00 PM", "Student Activity Quadrangle", 200, 0, "🕺", 199),
            Event(5, "Inter-College Futsal", "Sports", "5-a-side football tournament. Fast-paced knockout matches on turf.", "2026-11-14", "09:00 AM", "Sports Complex Turf", 16, 200, "⚽", 15),
            Event(6, "Box Cricket Cup", "Sports", "Over-arm gully cricket under floodlights. 6 overs per innings, 7 players a side.", "2026-11-15", "10:00 AM", "Main Grounds Box", 32, 100, "🏏", 31),
            Event(7, "FIFA 24 Tournament", "Gaming", "Console esports tournament on PS5. Single-elimination bracket, tactical matches.", "2026-11-14", "11:00 AM", "Gaming Lounge, Room 102", 64, 50, "🎮", 62),
            Event(8, "Valorant Showdown", "Gaming", "5v5 competitive shooter tournament. Custom lobbies, standard competitive rules.", "2026-11-15", "01:00 PM", "Computing Lab 1", 16, 0, "🎯", 15),
            Event(9, "Intro to Vibe Coding", "Workshop", "Hands-on session: building full-stack web and mobile apps using AI coding assistants.", "2026-11-14", "11:30 AM", "Seminar Hall A", 120, 0, "✨", 119),
            Event(10, "UI/UX Masterclass", "Workshop", "From wireframes to design systems in Figma. Interactive case studies by product designers.", "2026-11-15", "02:30 PM", "Design Studio, Block B", 80, 0, "🎨", 79),
            Event(11, "Acoustic Night", "Cultural", "Unplugged vocal and instrumental performances under the stars. Open-mic segment included.", "2026-11-14", "08:30 PM", "Amphitheatre Lawn", 300, 0, "🌙", 299),
            Event(12, "AI Pitch Fest", "Technical", "Startup ideas powered by generative AI. Pitch to a panel of founders and engineers.", "2026-11-15", "11:00 AM", "Auditorium", 40, 0, "💡", 39),
            Event(13, "Web Design Rumble", "Technical", "Timed front-end styling challenge. Replicate given mocks with CSS and JavaScript.", "2026-11-15", "03:00 PM", "Lab 2, Computing Block", 50, 0, "🌐", 49),
            Event(14, "Street Play (Nukkad)", "Cultural", "Original street theatre performances on social issues. High energy, live drums.", "2026-11-15", "05:00 PM", "Central Plaza", 150, 0, "🎭", 149)
        )
        events.addAll(initialList)

        // Seed sample registrations
        registrations.add(RegistrationItem(nextRegId++, 1, "Aarav Sharma", "2024BCSE042", "aarav.sharma@college.edu", "+91 9876543210", "2026-10-13 10:00:00", "Code Sprint 2.0"))
        registrations.add(RegistrationItem(nextRegId++, 2, "Priya Patel", "2024BIT019", "priya.p@college.edu", "+91 9812345678", "2026-10-13 10:30:00", "Robo Race"))
        registrations.add(RegistrationItem(nextRegId++, 3, "Rohan Verma", "2024BME088", "rohan.v@college.edu", null, "2026-10-13 11:00:00", "Battle of the Bands"))
    }

    @Synchronized
    fun getEvents(category: String?, search: String?): List<Event> {
        return events.filter { ev ->
            val matchCat = category.isNullOrBlank() || category.equals("All", ignoreCase = true) || ev.category.equals(category, ignoreCase = true)
            val matchSearch = search.isNullOrBlank() || ev.title.contains(search, ignoreCase = true) || (ev.description?.contains(search, ignoreCase = true) == true)
            matchCat && matchSearch
        }
    }

    @Synchronized
    fun getEventDetail(eventId: Int): Event? {
        return events.find { it.id == eventId }
    }

    @Synchronized
    fun registerForEvent(eventId: Int, payload: RegistrationIn): Pair<Boolean, Any> {
        val eventIndex = events.indexOfFirst { it.id == eventId }
        if (eventIndex == -1) {
            return Pair(false, "Event with id $eventId not found")
        }
        val event = events[eventIndex]

        if (payload.studentName.isBlank() || payload.rollNumber.isBlank() || payload.email.isBlank()) {
            return Pair(false, "Student name, roll number, and email are required")
        }
        if (!payload.email.contains("@") || !payload.email.contains(".")) {
            return Pair(false, "Invalid email address: must contain '@' and a valid domain")
        }
        if (event.availableSeats <= 0) {
            return Pair(false, "Event is full")
        }
        val already = registrations.any { it.eventId == eventId && it.email.equals(payload.email.trim(), ignoreCase = true) }
        if (already) {
            return Pair(false, "Already registered for this event")
        }

        // Decrement seats
        val updatedEvent = event.copy(availableSeats = event.availableSeats - 1)
        events[eventIndex] = updatedEvent

        val timeStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val reg = RegistrationItem(
            id = nextRegId++,
            eventId = eventId,
            studentName = payload.studentName.trim(),
            rollNumber = payload.rollNumber.trim(),
            email = payload.email.trim(),
            phone = payload.phone?.trim(),
            registeredAt = timeStr,
            eventTitle = event.title
        )
        registrations.add(reg)

        val out = RegistrationOut(
            id = reg.id,
            eventId = reg.eventId,
            studentName = reg.studentName,
            registeredAt = reg.registeredAt
        )
        return Pair(true, out)
    }

    @Synchronized
    fun getRegistrations(eventId: Int?): List<RegistrationItem> {
        return if (eventId == null || eventId == 0) {
            registrations.toList()
        } else {
            registrations.filter { it.eventId == eventId }
        }
    }

    @Synchronized
    fun getStats(): Stats {
        val totalEvents = events.size
        val totalRegistrations = registrations.size
        val totalCapacity = events.sumOf { it.capacity }
        val utilPct = if (totalCapacity > 0) {
            ((totalRegistrations.toDouble() / totalCapacity.toDouble()) * 1000.0).toInt() / 10.0
        } else 0.0

        val categoryMap = mutableMapOf<String, Int>()
        events.forEach { ev ->
            categoryMap[ev.category] = (categoryMap[ev.category] ?: 0) + 1
        }

        return Stats(
            totalEvents = totalEvents,
            totalRegistrations = totalRegistrations,
            totalCapacity = totalCapacity,
            overallUtilizationPct = utilPct,
            upcomingEventsCount = totalEvents,
            eventsByCategory = categoryMap
        )
    }

    @Synchronized
    fun createEvent(payload: EventCreate): Event {
        val newEvent = Event(
            id = nextEventId++,
            title = payload.title,
            category = payload.category,
            description = payload.description,
            eventDate = payload.eventDate,
            eventTime = payload.eventTime,
            venue = payload.venue,
            capacity = payload.capacity,
            fee = payload.fee,
            icon = payload.icon,
            availableSeats = payload.capacity
        )
        events.add(newEvent)
        return newEvent
    }

    @Synchronized
    fun cancelRegistration(registrationId: Int): Pair<Boolean, String> {
        val reg = registrations.find { it.id == registrationId }
            ?: return Pair(false, "Registration $registrationId not found")

        registrations.remove(reg)
        val eventIndex = events.indexOfFirst { it.id == reg.eventId }
        if (eventIndex != -1) {
            val ev = events[eventIndex]
            if (ev.availableSeats < ev.capacity) {
                events[eventIndex] = ev.copy(availableSeats = ev.availableSeats + 1)
            }
        }
        return Pair(true, "Registration cancelled successfully")
    }
}
