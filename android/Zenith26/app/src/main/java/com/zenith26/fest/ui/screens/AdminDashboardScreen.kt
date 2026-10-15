package com.zenith26.fest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.zenith26.fest.ui.components.AddEventModal
import com.zenith26.fest.ui.components.AdminEventsTableView
import com.zenith26.fest.ui.components.AdminRegistrationsTableView
import com.zenith26.fest.ui.components.AdminStatCards
import com.zenith26.fest.ui.components.ApiConfigDialog
import com.zenith26.fest.ui.components.CategoryBreakdownView
import com.zenith26.fest.ui.components.LoadingStateView
import com.zenith26.fest.ui.components.ZenithNavbar
import com.zenith26.fest.ui.theme.CatTechnicalBg
import com.zenith26.fest.ui.theme.CatTechnicalText
import com.zenith26.fest.ui.theme.FestBg
import com.zenith26.fest.ui.theme.FestBorder
import com.zenith26.fest.ui.theme.FestCyan
import com.zenith26.fest.ui.theme.FestSurface
import com.zenith26.fest.ui.theme.FestSurfaceLight
import com.zenith26.fest.ui.theme.FestTextMuted
import com.zenith26.fest.ui.theme.FestTextPrimary
import com.zenith26.fest.ui.theme.FestTextSecondary
import com.zenith26.fest.ui.theme.PrimaryButtonGradient
import com.zenith26.fest.ui.viewmodel.AdminTab
import com.zenith26.fest.ui.viewmodel.AdminViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onNavigateToStudent: () -> Unit,
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
                isAdminMode = true,
                onToggleMode = onNavigateToStudent,
                onOpenSettings = { isSettingsOpen = true }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header & Action Row
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Overview & Analytics",
                                color = FestTextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Real-time metrics, capacity utilization, and registration management.",
                                color = FestTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Refresh button
                            IconButton(
                                onClick = { viewModel.refreshAll() },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FestSurface)
                                    .border(1.dp, FestBorder, RoundedCornerShape(8.dp))
                            ) {
                                if (uiState.isRefreshing) {
                                    CircularProgressIndicator(
                                        color = FestCyan,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh",
                                        tint = FestTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Stretch Goal: Create New Event button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryButtonGradient)
                                    .clickable { viewModel.openAddEventModal() }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "New Event",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // 4 Live Stat Cards
                item {
                    AdminStatCards(stats = uiState.stats)
                }

                // Category Breakdown
                item {
                    CategoryBreakdownView(categoryCounts = uiState.stats?.eventsByCategory)
                }

                // Tabs: Events vs Raw Registrations
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 1.dp, color = FestBorder.copy(alpha = 0.5f))
                            .padding(bottom = 2.dp)
                    ) {
                        // Tab 1: Events & Capacity
                        val isEventsTab = uiState.selectedTab == AdminTab.EVENTS
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectTab(AdminTab.EVENTS) }
                                .padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Events & Capacity",
                                    color = if (isEventsTab) FestCyan else FestTextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isEventsTab) CatTechnicalBg else FestSurfaceLight)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${uiState.eventsList.size}",
                                        color = if (isEventsTab) CatTechnicalText else FestTextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(2.dp)
                                    .background(if (isEventsTab) FestCyan else Color.Transparent)
                            )
                        }

                        // Tab 2: Registrations
                        val isRegTab = uiState.selectedTab == AdminTab.REGISTRATIONS
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectTab(AdminTab.REGISTRATIONS) }
                                .padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Raw Registrations",
                                    color = if (isRegTab) FestCyan else FestTextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isRegTab) CatTechnicalBg else FestSurfaceLight)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${uiState.registrations.size}",
                                        color = if (isRegTab) CatTechnicalText else FestTextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(2.dp)
                                    .background(if (isRegTab) FestCyan else Color.Transparent)
                            )
                        }
                    }
                }

                // Active Tab Content
                when (uiState.selectedTab) {
                    AdminTab.EVENTS -> {
                        if (uiState.isLoadingEvents && uiState.eventsList.isEmpty()) {
                            item {
                                LoadingStateView(message = "Loading events...")
                            }
                        } else {
                            item {
                                AdminEventsTableView(eventsList = uiState.eventsList)
                            }
                        }
                    }

                    AdminTab.REGISTRATIONS -> {
                        if (uiState.isLoadingRegistrations && uiState.registrations.isEmpty()) {
                            item {
                                LoadingStateView(message = "Loading registrations...")
                            }
                        } else {
                            item {
                                AdminRegistrationsTableView(
                                    registrations = uiState.registrations,
                                    rawEvents = uiState.rawEvents,
                                    selectedEventId = uiState.selectedEventFilterId,
                                    onSelectEventFilter = { viewModel.filterRegistrationsByEvent(it) },
                                    onCancelRegistration = { regId ->
                                        viewModel.cancelRegistration(regId) {
                                            viewModel.loadDashboardData()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Add Event Modal (Stretch Goal)
        AddEventModal(
            isOpen = uiState.isAddEventModalOpen,
            isSubmitting = uiState.isSubmittingAddEvent,
            alert = uiState.addEventAlert,
            onDismiss = { viewModel.closeAddEventModal() },
            onSubmit = { title, cat, icon, date, time, venue, cap, fee, desc ->
                viewModel.createNewEvent(title, cat, icon, date, time, venue, cap, fee, desc)
            }
        )

        // API Base URL Settings Dialog
        ApiConfigDialog(
            isOpen = isSettingsOpen,
            onDismiss = { isSettingsOpen = false },
            onUrlUpdated = {
                viewModel.loadDashboardData()
            }
        )
    }
}
