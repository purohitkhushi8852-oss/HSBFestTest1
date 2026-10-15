package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.RegistrationItem
import com.zenith26.fest.ui.theme.CatCulturalBg
import com.zenith26.fest.ui.theme.CatCulturalBorder
import com.zenith26.fest.ui.theme.CatCulturalText
import com.zenith26.fest.ui.theme.CatGamingBg
import com.zenith26.fest.ui.theme.CatGamingBorder
import com.zenith26.fest.ui.theme.CatGamingText
import com.zenith26.fest.ui.theme.CatSportsBg
import com.zenith26.fest.ui.theme.CatSportsBorder
import com.zenith26.fest.ui.theme.CatSportsText
import com.zenith26.fest.ui.theme.CatTechnicalBg
import com.zenith26.fest.ui.theme.CatTechnicalBorder
import com.zenith26.fest.ui.theme.CatTechnicalText
import com.zenith26.fest.ui.theme.CatWorkshopBg
import com.zenith26.fest.ui.theme.CatWorkshopBorder
import com.zenith26.fest.ui.theme.CatWorkshopText
import com.zenith26.fest.ui.theme.FestAmber
import com.zenith26.fest.ui.theme.FestAmberBg
import com.zenith26.fest.ui.theme.FestAmberText
import com.zenith26.fest.ui.theme.FestBorder
import com.zenith26.fest.ui.theme.FestCyan
import com.zenith26.fest.ui.theme.FestEmerald
import com.zenith26.fest.ui.theme.FestRose
import com.zenith26.fest.ui.theme.FestRoseBg
import com.zenith26.fest.ui.theme.FestRoseText
import com.zenith26.fest.ui.theme.FestSurface
import com.zenith26.fest.ui.theme.FestSurfaceLight
import com.zenith26.fest.ui.theme.FestTextMuted
import com.zenith26.fest.ui.theme.FestTextPrimary
import com.zenith26.fest.ui.theme.FestTextSecondary
import com.zenith26.fest.ui.theme.FestViolet
import com.zenith26.fest.ui.viewmodel.AdminEventRow

@Composable
fun AdminEventsTableView(
    eventsList: List<AdminEventRow>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Warning description banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sorted by Utilization % Descending",
                color = FestTextMuted,
                fontSize = 11.sp
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(FestAmberBg.copy(alpha = 0.7f))
                    .border(1.dp, Color(0x66F59E0B), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(FestAmber)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Over 90% Warning",
                        color = FestAmberText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Table container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(FestSurface.copy(alpha = 0.9f))
                .border(1.dp, FestBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                eventsList.forEachIndexed { index, rowItem ->
                    val ev = rowItem.event
                    val (catBg, catText, catBorder) = when (ev.category.lowercase()) {
                        "technical" -> Triple(CatTechnicalBg, CatTechnicalText, CatTechnicalBorder)
                        "cultural" -> Triple(CatCulturalBg, CatCulturalText, CatCulturalBorder)
                        "sports" -> Triple(CatSportsBg, CatSportsText, CatSportsBorder)
                        "gaming" -> Triple(CatGamingBg, CatGamingText, CatGamingBorder)
                        "workshop" -> Triple(CatWorkshopBg, CatWorkshopText, CatWorkshopBorder)
                        else -> Triple(FestSurfaceLight, FestTextSecondary, FestBorder)
                    }

                    val rowBackground = if (rowItem.isFull) {
                        FestRoseBg.copy(alpha = 0.25f)
                    } else if (rowItem.isHighDemand) {
                        FestAmberBg.copy(alpha = 0.2f)
                    } else {
                        Color.Transparent
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(rowBackground)
                            .padding(14.dp)
                    ) {
                        // Top line: Icon, Title, ID, Category badge, Utilization
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (!ev.icon.isNullOrBlank()) ev.icon else "⚡",
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = ev.title,
                                        color = FestTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "ID: #${ev.id}",
                                        color = FestTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Badges: Category & Utilization
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(catBg)
                                        .border(1.dp, catBorder, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = ev.category.uppercase(),
                                        color = catText,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                if (rowItem.isFull) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(FestRoseBg)
                                            .border(1.dp, Color(0x66F43F5E), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "100% FULL",
                                            color = FestRoseText,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else if (rowItem.isHighDemand) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(FestAmberBg)
                                            .border(1.dp, Color(0x66F59E0B), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "⚠️ ${rowItem.utilizationPct}%",
                                            color = FestAmberText,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "${rowItem.utilizationPct}%",
                                        color = FestTextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Details row: Reg/Cap, Venue, Fee
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reg: ${rowItem.registeredCount} / ${ev.capacity}",
                                color = if (rowItem.isFull) FestRoseText else FestTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = ev.venue,
                                color = FestTextMuted,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            Text(
                                text = if (ev.fee == 0) "Free" else "₹${ev.fee}",
                                color = if (ev.fee == 0) FestEmerald else FestCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (index < eventsList.size - 1) {
                        HorizontalDivider(color = FestBorder.copy(alpha = 0.4f), thickness = 1.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminRegistrationsTableView(
    registrations: List<RegistrationItem>,
    rawEvents: List<Event>,
    selectedEventId: Int?,
    onSelectEventFilter: (Int?) -> Unit,
    onCancelRegistration: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedDropdown by remember { mutableStateOf(false) }
    var itemToCancel by remember { mutableStateOf<RegistrationItem?>(null) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Filter bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(FestSurface.copy(alpha = 0.9f))
                .border(1.dp, FestBorder, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dropdown selector
                Box {
                    val selectedTitle = if (selectedEventId == null) {
                        "All Events (${registrations.size})"
                    } else {
                        val ev = rawEvents.find { it.id == selectedEventId }
                        ev?.title ?: "Event #${selectedEventId}"
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF020617))
                            .border(1.dp, FestBorder, RoundedCornerShape(10.dp))
                            .clickable { expandedDropdown = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedTitle,
                            color = FestTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = FestTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.background(FestSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Events", color = FestTextPrimary, fontSize = 12.sp) },
                            onClick = {
                                onSelectEventFilter(null)
                                expandedDropdown = false
                            }
                        )
                        rawEvents.forEach { ev ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "${ev.icon ?: "⚡"} ${ev.title}",
                                        color = FestTextPrimary,
                                        fontSize = 12.sp
                                    )
                                },
                                onClick = {
                                    onSelectEventFilter(ev.id)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "${registrations.size} Signups",
                    color = FestCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Registrations list
        if (registrations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No registrations found for this filter.",
                    color = FestTextMuted,
                    fontSize = 12.sp
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(FestSurface.copy(alpha = 0.9f))
                    .border(1.dp, FestBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    registrations.forEachIndexed { index, reg ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${reg.id}",
                                        color = FestTextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = reg.studentName,
                                        color = FestTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "${reg.rollNumber} • ${reg.email}",
                                    color = FestTextSecondary,
                                    fontSize = 10.sp
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = reg.eventTitle,
                                    color = FestViolet,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Stretch Goal 2: Cancel Registration Action
                            IconButton(
                                onClick = { itemToCancel = reg },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Cancel registration",
                                    tint = FestRoseText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        if (index < registrations.size - 1) {
                            HorizontalDivider(color = FestBorder.copy(alpha = 0.4f), thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }

    // Cancel Confirmation Dialog
    if (itemToCancel != null) {
        val reg = itemToCancel!!
        AlertDialog(
            onDismissRequest = { itemToCancel = null },
            title = {
                Text(
                    text = "Cancel Registration?",
                    color = FestTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to cancel the registration for \"${reg.studentName}\" in \"${reg.eventTitle}\"? This will immediately free up 1 seat.",
                    color = FestTextSecondary,
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val regId = reg.id
                        itemToCancel = null
                        onCancelRegistration(regId)
                    }
                ) {
                    Text(text = "Confirm Cancel", color = FestRose, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToCancel = null }) {
                    Text(text = "Dismiss", color = FestTextMuted)
                }
            },
            containerColor = FestSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
