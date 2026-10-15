package com.zenith26.fest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.RegistrationIn
import com.zenith26.fest.data.model.RegistrationOut
import com.zenith26.fest.data.repository.ZenithRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AlertType {
    SUCCESS, ERROR, WARNING
}

data class RegistrationUiState(
    val isSubmitting: Boolean = false,
    val alertType: AlertType? = null,
    val alertMessage: String? = null,
    val successConfirmation: RegistrationOut? = null
)

data class StudentUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val activeModalEvent: Event? = null,
    val isLoadingModalEvent: Boolean = false,
    val isModalOpen: Boolean = false,
    val registrationState: RegistrationUiState = RegistrationUiState()
)

class StudentViewModel(
    private val repository: ZenithRepository = ZenithRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadEvents()
    }

    fun loadEvents() {
        val cat = _uiState.value.selectedCategory
        val search = _uiState.value.searchQuery

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.getEvents(cat, search)
            result.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        events = list,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Failed to connect to the backend server. Make sure FastAPI is running on port 8000."
                    )
                }
            }
        }
    }

    fun selectCategory(category: String) {
        if (_uiState.value.selectedCategory == category) return
        _uiState.update { it.copy(selectedCategory = category) }
        loadEvents()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(250) // Debounce matching web app 250ms
            loadEvents()
        }
    }

    fun resetFilters() {
        _uiState.update {
            it.copy(
                selectedCategory = "All",
                searchQuery = ""
            )
        }
        loadEvents()
    }

    fun openEventDetails(eventId: Int) {
        _uiState.update {
            it.copy(
                isModalOpen = true,
                isLoadingModalEvent = true,
                registrationState = RegistrationUiState()
            )
        }

        viewModelScope.launch {
            val result = repository.getEventDetail(eventId)
            result.onSuccess { event ->
                val warningAlert = if (event.availableSeats <= 0) {
                    RegistrationUiState(
                        alertType = AlertType.WARNING,
                        alertMessage = "This event has reached full capacity. No further registrations can be accepted."
                    )
                } else {
                    RegistrationUiState()
                }

                _uiState.update {
                    it.copy(
                        activeModalEvent = event,
                        isLoadingModalEvent = false,
                        registrationState = warningAlert
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoadingModalEvent = false,
                        registrationState = RegistrationUiState(
                            alertType = AlertType.ERROR,
                            alertMessage = "Could not load event details: ${ex.message}"
                        )
                    )
                }
            }
        }
    }

    fun closeModal() {
        _uiState.update {
            it.copy(
                isModalOpen = false,
                activeModalEvent = null,
                registrationState = RegistrationUiState()
            )
        }
    }

    fun registerStudent(name: String, roll: String, email: String, phone: String?) {
        val currentEvent = _uiState.value.activeModalEvent ?: return

        // Client-side validations
        if (name.isBlank()) {
            _uiState.update {
                it.copy(
                    registrationState = RegistrationUiState(
                        alertType = AlertType.ERROR,
                        alertMessage = "Student name is required."
                    )
                )
            }
            return
        }

        if (roll.isBlank()) {
            _uiState.update {
                it.copy(
                    registrationState = RegistrationUiState(
                        alertType = AlertType.ERROR,
                        alertMessage = "Roll number is required."
                    )
                )
            }
            return
        }

        if (email.isBlank()) {
            _uiState.update {
                it.copy(
                    registrationState = RegistrationUiState(
                        alertType = AlertType.ERROR,
                        alertMessage = "Email address is required."
                    )
                )
            }
            return
        }

        if (!email.contains("@") || !email.contains(".")) {
            _uiState.update {
                it.copy(
                    registrationState = RegistrationUiState(
                        alertType = AlertType.ERROR,
                        alertMessage = "Please enter a valid email address containing '@' and a domain."
                    )
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                registrationState = it.registrationState.copy(isSubmitting = true)
            )
        }

        val payload = RegistrationIn(
            studentName = name.trim(),
            rollNumber = roll.trim(),
            email = email.trim().lowercase(),
            phone = phone?.trim()?.takeIf { it.isNotBlank() }
        )

        viewModelScope.launch {
            val result = repository.registerForEvent(currentEvent.id, payload)
            result.onSuccess { regOut ->
                _uiState.update {
                    it.copy(
                        registrationState = RegistrationUiState(
                            isSubmitting = false,
                            alertType = AlertType.SUCCESS,
                            alertMessage = "Confirmed! ${regOut.studentName} is registered for ${currentEvent.title}. Ticket ID: #${regOut.id}",
                            successConfirmation = regOut
                        )
                    )
                }

                // Immediately re-fetch events to update live available seats count
                loadEvents()

                // Re-fetch active modal event to update seats display
                val updatedDetail = repository.getEventDetail(currentEvent.id)
                updatedDetail.onSuccess { ev ->
                    _uiState.update { it.copy(activeModalEvent = ev) }
                }

                // Auto dismiss modal after brief moment to match web behavior (1.8s)
                delay(1800)
                closeModal()
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        registrationState = RegistrationUiState(
                            isSubmitting = false,
                            alertType = AlertType.ERROR,
                            alertMessage = ex.message ?: "Failed to register"
                        )
                    )
                }
            }
        }
    }
}
