package com.zenith26.fest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.EventCreate
import com.zenith26.fest.data.model.RegistrationItem
import com.zenith26.fest.data.model.Stats
import com.zenith26.fest.data.repository.ZenithRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.round

enum class AdminTab {
    EVENTS, REGISTRATIONS
}

data class AdminEventRow(
    val event: Event,
    val registeredCount: Int,
    val utilizationPct: Double,
    val isHighDemand: Boolean,
    val isFull: Boolean
)

data class AdminUiState(
    val stats: Stats? = null,
    val isLoadingStats: Boolean = false,
    val eventsList: List<AdminEventRow> = emptyList(),
    val rawEvents: List<Event> = emptyList(),
    val isLoadingEvents: Boolean = false,
    val eventsErrorMessage: String? = null,
    val registrations: List<RegistrationItem> = emptyList(),
    val isLoadingRegistrations: Boolean = false,
    val regErrorMessage: String? = null,
    val selectedTab: AdminTab = AdminTab.EVENTS,
    val selectedEventFilterId: Int? = null,
    val isAddEventModalOpen: Boolean = false,
    val isSubmittingAddEvent: Boolean = false,
    val addEventAlert: Pair<AlertType, String>? = null,
    val isRefreshing: Boolean = false
)

class AdminViewModel(
    private val repository: ZenithRepository = ZenithRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        fetchStats()
        fetchEvents()
        fetchRegistrations(_uiState.value.selectedEventFilterId)
    }

    fun refreshAll() {
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            fetchStats()
            fetchEvents()
            fetchRegistrations(_uiState.value.selectedEventFilterId)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun selectTab(tab: AdminTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        if (tab == AdminTab.REGISTRATIONS) {
            fetchRegistrations(_uiState.value.selectedEventFilterId)
        }
    }

    fun filterRegistrationsByEvent(eventId: Int?) {
        _uiState.update { it.copy(selectedEventFilterId = eventId) }
        fetchRegistrations(eventId)
    }

    private fun fetchStats() {
        _uiState.update { it.copy(isLoadingStats = true) }
        viewModelScope.launch {
            val result = repository.getStats()
            result.onSuccess { stats ->
                _uiState.update { it.copy(stats = stats, isLoadingStats = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoadingStats = false) }
            }
        }
    }

    private fun fetchEvents() {
        _uiState.update { it.copy(isLoadingEvents = true, eventsErrorMessage = null) }
        viewModelScope.launch {
            val result = repository.getEvents()
            result.onSuccess { events ->
                val processed = events.map { ev ->
                    val registered = ev.capacity - ev.availableSeats
                    val utilRaw = if (ev.capacity > 0) (registered.toDouble() / ev.capacity.toDouble()) * 100.0 else 0.0
                    val utilPct = round(utilRaw * 10.0) / 10.0
                    val isFull = ev.availableSeats == 0
                    val isHighDemand = utilPct >= 90.0

                    AdminEventRow(
                        event = ev,
                        registeredCount = registered,
                        utilizationPct = utilPct,
                        isHighDemand = isHighDemand,
                        isFull = isFull
                    )
                }.sortedByDescending { it.utilizationPct } // Sort by utilization % descending matching web

                _uiState.update {
                    it.copy(
                        eventsList = processed,
                        rawEvents = events,
                        isLoadingEvents = false,
                        eventsErrorMessage = null
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoadingEvents = false,
                        eventsErrorMessage = ex.message ?: "Failed to load events"
                    )
                }
            }
        }
    }

    fun fetchRegistrations(eventId: Int?) {
        _uiState.update { it.copy(isLoadingRegistrations = true, regErrorMessage = null) }
        viewModelScope.launch {
            val result = repository.getRegistrations(eventId)
            result.onSuccess { regs ->
                _uiState.update {
                    it.copy(
                        registrations = regs,
                        isLoadingRegistrations = false,
                        regErrorMessage = null
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoadingRegistrations = false,
                        regErrorMessage = ex.message ?: "Failed to load registrations"
                    )
                }
            }
        }
    }

    fun cancelRegistration(registrationId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.cancelRegistration(registrationId)
            result.onSuccess {
                loadDashboardData()
                onSuccess()
            }
        }
    }

    fun openAddEventModal() {
        _uiState.update {
            it.copy(
                isAddEventModalOpen = true,
                addEventAlert = null
            )
        }
    }

    fun closeAddEventModal() {
        _uiState.update {
            it.copy(
                isAddEventModalOpen = false,
                addEventAlert = null
            )
        }
    }

    fun createNewEvent(
        title: String,
        category: String,
        icon: String,
        date: String,
        time: String,
        venue: String,
        capacity: Int,
        fee: Int,
        description: String
    ) {
        if (title.isBlank() || category.isBlank() || venue.isBlank() || date.isBlank() || time.isBlank()) {
            _uiState.update {
                it.copy(
                    addEventAlert = Pair(
                        AlertType.ERROR,
                        "Title, category, venue, date, and time are required."
                    )
                )
            }
            return
        }

        if (capacity <= 0) {
            _uiState.update {
                it.copy(
                    addEventAlert = Pair(
                        AlertType.ERROR,
                        "Capacity must be greater than 0."
                    )
                )
            }
            return
        }

        _uiState.update { it.copy(isSubmittingAddEvent = true) }

        val payload = EventCreate(
            title = title.trim(),
            category = category.trim(),
            icon = icon.ifBlank { "🎉" },
            eventDate = date.trim(),
            eventTime = time.trim(),
            venue = venue.trim(),
            capacity = capacity,
            fee = fee,
            description = description.trim()
        )

        viewModelScope.launch {
            val result = repository.createEvent(payload)
            result.onSuccess { newEvent ->
                _uiState.update {
                    it.copy(
                        isSubmittingAddEvent = false,
                        addEventAlert = Pair(
                            AlertType.SUCCESS,
                            "Event \"${newEvent.title}\" created successfully!"
                        )
                    )
                }
                loadDashboardData()
                kotlinx.coroutines.delay(1200)
                closeAddEventModal()
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isSubmittingAddEvent = false,
                        addEventAlert = Pair(
                            AlertType.ERROR,
                            ex.message ?: "Failed to create event"
                        )
                    )
                }
            }
        }
    }
}
