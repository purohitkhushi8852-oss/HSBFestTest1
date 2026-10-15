package com.zenith26.fest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.components.ApiConfigDialog
import com.zenith26.fest.ui.components.CategoryFilterBar
import com.zenith26.fest.ui.components.EmptyStateView
import com.zenith26.fest.ui.components.ErrorStateView
import com.zenith26.fest.ui.components.EventCard
import com.zenith26.fest.ui.components.EventDetailModal
import com.zenith26.fest.ui.components.HeroSection
import com.zenith26.fest.ui.components.LoadingStateView
import com.zenith26.fest.ui.components.SearchBar
import com.zenith26.fest.ui.components.ZenithNavbar
import com.zenith26.fest.ui.theme.CatTechnicalBg
import com.zenith26.fest.ui.theme.CatTechnicalBorder
import com.zenith26.fest.ui.theme.CatTechnicalText
import com.zenith26.fest.ui.theme.FestBg
import com.zenith26.fest.ui.theme.FestSurface
import com.zenith26.fest.ui.theme.FestTextMuted
import com.zenith26.fest.ui.viewmodel.StudentViewModel

@Composable
fun StudentPortalScreen(
    viewModel: StudentViewModel,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var isSettingsOpen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FestBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Navbar
            ZenithNavbar(
                isAdminMode = false,
                onToggleMode = onNavigateToAdmin,
                onOpenSettings = { isSettingsOpen = true }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Section
                item {
                    HeroSection(totalEventsCount = uiState.events.size)
                }

                // Search & Filter Box Container
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(FestSurface.copy(alpha = 0.9f))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Search input
                            SearchBar(
                                query = uiState.searchQuery,
                                onQueryChange = { viewModel.onSearchQueryChanged(it) }
                            )

                            // Category filter pills
                            CategoryFilterBar(
                                selectedCategory = uiState.selectedCategory,
                                onCategorySelected = { viewModel.selectCategory(it) }
                            )
                        }
                    }
                }

                // Active Filter Status & Event Count Bar
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statusText = buildString {
                            append("Showing ")
                            if (uiState.selectedCategory != "All") {
                                append("${uiState.selectedCategory} events")
                            } else {
                                append("all events")
                            }
                            if (uiState.searchQuery.isNotBlank()) {
                                append(" matching \"${uiState.searchQuery}\"")
                            }
                        }

                        Text(
                            text = statusText,
                            color = FestTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(CatTechnicalBg)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${uiState.events.size} Event${if (uiState.events.size == 1) "" else "s"}",
                                color = CatTechnicalText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Content States: Loading, Error, Empty, or List of Cards
                when {
                    uiState.isLoading -> {
                        item {
                            LoadingStateView(message = "Fetching events from server...")
                        }
                    }

                    uiState.errorMessage != null -> {
                        item {
                            ErrorStateView(
                                errorMessage = uiState.errorMessage!!,
                                onRetry = { viewModel.loadEvents() }
                            )
                        }
                    }

                    uiState.events.isEmpty() -> {
                        item {
                            EmptyStateView(
                                onResetFilters = { viewModel.resetFilters() }
                            )
                        }
                    }

                    else -> {
                        items(uiState.events, key = { it.id }) { event ->
                            EventCard(
                                event = event,
                                onViewDetails = { viewModel.openEventDetails(it) }
                            )
                        }
                    }
                }

                // Footer spacing
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Event Details & Registration Modal Dialog
        if (uiState.isModalOpen) {
            EventDetailModal(
                event = uiState.activeModalEvent,
                isLoadingEvent = uiState.isLoadingModalEvent,
                registrationState = uiState.registrationState,
                onDismiss = { viewModel.closeModal() },
                onSubmitRegistration = { name, roll, email, phone ->
                    viewModel.registerStudent(name, roll, email, phone)
                }
            )
        }

        // API Base URL Settings Dialog
        ApiConfigDialog(
            isOpen = isSettingsOpen,
            onDismiss = { isSettingsOpen = false },
            onUrlUpdated = {
                viewModel.loadEvents()
            }
        )
    }
}
