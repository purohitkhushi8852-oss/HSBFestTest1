package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.ui.theme.CatTechnicalBg
import com.zenith26.fest.ui.theme.CatTechnicalBorder
import com.zenith26.fest.ui.theme.CatTechnicalText
import com.zenith26.fest.ui.theme.FestAmber
import com.zenith26.fest.ui.theme.FestAmberBg
import com.zenith26.fest.ui.theme.FestAmberText
import com.zenith26.fest.ui.theme.FestBorder
import com.zenith26.fest.ui.theme.FestCyan
import com.zenith26.fest.ui.theme.FestEmerald
import com.zenith26.fest.ui.theme.FestEmeraldBg
import com.zenith26.fest.ui.theme.FestEmeraldText
import com.zenith26.fest.ui.theme.FestRose
import com.zenith26.fest.ui.theme.FestRoseBg
import com.zenith26.fest.ui.theme.FestRoseText
import com.zenith26.fest.ui.theme.FestSurface
import com.zenith26.fest.ui.theme.FestSurfaceLight
import com.zenith26.fest.ui.theme.FestTextMuted
import com.zenith26.fest.ui.theme.FestTextPrimary
import com.zenith26.fest.ui.theme.FestTextSecondary
import com.zenith26.fest.ui.theme.ModalHeaderGradient
import com.zenith26.fest.ui.theme.PrimaryButtonGradient
import com.zenith26.fest.ui.viewmodel.AlertType
import com.zenith26.fest.ui.viewmodel.RegistrationUiState

@Composable
fun EventDetailModal(
    event: Event?,
    isLoadingEvent: Boolean,
    registrationState: RegistrationUiState,
    onDismiss: () -> Unit,
    onSubmitRegistration: (name: String, roll: String, email: String, phone: String?) -> Unit
) {
    if (event == null && !isLoadingEvent) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, FestBorder, RoundedCornerShape(24.dp)),
            color = FestSurface
        ) {
            if (isLoadingEvent || event == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = FestCyan,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(36.dp)
                    )
                }
                return@Surface
            }

            var nameInput by remember { mutableStateOf("") }
            var rollInput by remember { mutableStateOf("") }
            var emailInput by remember { mutableStateOf("") }
            var phoneInput by remember { mutableStateOf("") }

            var nameError by remember { mutableStateOf<String?>(null) }
            var rollError by remember { mutableStateOf<String?>(null) }
            var emailError by remember { mutableStateOf<String?>(null) }

            val isFull = event.availableSeats <= 0

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Modal Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ModalHeaderGradient)
                        .border(1.dp, FestBorder.copy(alpha = 0.8f))
                        .padding(18.dp)
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(FestSurfaceLight.copy(alpha = 0.8f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = FestTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(FestSurfaceLight)
                                .border(1.dp, FestBorder, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (!event.icon.isNullOrBlank()) event.icon else "🎉",
                                fontSize = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CatTechnicalBg)
                                    .border(1.dp, CatTechnicalBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = event.category.uppercase(),
                                    color = CatTechnicalText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = event.title,
                                color = FestTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                // Modal Body Content
                Column(modifier = Modifier.padding(18.dp)) {
                    // 2x2 Metadata Details Grid
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF020617)) // bg-slate-950
                            .border(1.dp, FestBorder.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Date & Time", color = FestTextMuted, fontSize = 11.sp)
                                    Text(
                                        text = "${event.eventDate}, ${event.eventTime}",
                                        color = FestTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Venue", color = FestTextMuted, fontSize = 11.sp)
                                    Text(
                                        text = event.venue,
                                        color = FestTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Entry Fee", color = FestTextMuted, fontSize = 11.sp)
                                    Text(
                                        text = if (event.fee == 0) "Free Entry" else "₹${event.fee}",
                                        color = if (event.fee == 0) FestEmerald else FestCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Available Seats", color = FestTextMuted, fontSize = 11.sp)
                                    Text(
                                        text = "${event.availableSeats} / ${event.capacity}",
                                        color = if (isFull) FestRoseText else FestCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // About This Event Description
                    Text(
                        text = "ABOUT THIS EVENT",
                        color = FestTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (!event.description.isNullOrBlank()) event.description else "No description provided.",
                        color = FestTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = FestBorder.copy(alpha = 0.6f), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Registration Form Section
                    Text(
                        text = "Register for this Event",
                        color = FestTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Complete the fields below to secure your spot. Instant confirmation!",
                        color = FestTextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Form Alert Feedback Box
                    if (registrationState.alertMessage != null) {
                        val (alertBg, alertBorder, alertText, alertIcon) = when (registrationState.alertType) {
                            AlertType.SUCCESS -> Quad(FestEmeraldBg, Color(0x6610B981), FestEmeraldText, "🎉")
                            AlertType.ERROR -> Quad(FestRoseBg, Color(0x66F43F5E), FestRoseText, "⚠️")
                            AlertType.WARNING -> Quad(FestAmberBg, Color(0x66F59E0B), FestAmberText, "ℹ️")
                            null -> Quad(FestSurfaceLight, FestBorder, FestTextSecondary, "")
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(alertBg)
                                .border(1.dp, alertBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = alertIcon, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = registrationState.alertMessage,
                                    color = alertText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Form Fields
                    InputField(
                        label = "Student Name",
                        isRequired = true,
                        placeholder = "e.g. Aarav Sharma",
                        value = nameInput,
                        onValueChange = {
                            nameInput = it
                            nameError = null
                        },
                        errorMessage = nameError
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    InputField(
                        label = "Roll Number",
                        isRequired = true,
                        placeholder = "e.g. 2024BCSE042",
                        value = rollInput,
                        onValueChange = {
                            rollInput = it
                            rollError = null
                        },
                        errorMessage = rollError
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    InputField(
                        label = "Email Address",
                        isRequired = true,
                        placeholder = "e.g. aarav@college.edu",
                        value = emailInput,
                        keyboardType = KeyboardType.Email,
                        onValueChange = {
                            emailInput = it
                            emailError = null
                        },
                        errorMessage = emailError
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    InputField(
                        label = "Phone Number (Optional)",
                        isRequired = false,
                        placeholder = "e.g. +91 98765 43210",
                        value = phoneInput,
                        keyboardType = KeyboardType.Phone,
                        onValueChange = { phoneInput = it },
                        errorMessage = null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (isFull) {
                                    Modifier
                                        .background(FestSurfaceLight)
                                        .border(1.dp, FestBorder, RoundedCornerShape(12.dp))
                                } else {
                                    Modifier.background(PrimaryButtonGradient)
                                }
                            )
                            .clickable(enabled = !isFull && !registrationState.isSubmitting) {
                                // Validate required fields
                                var valid = true
                                if (nameInput.isBlank()) {
                                    nameError = "Student name is required."
                                    valid = false
                                }
                                if (rollInput.isBlank()) {
                                    rollError = "Roll number is required."
                                    valid = false
                                }
                                if (emailInput.isBlank()) {
                                    emailError = "Email address is required."
                                    valid = false
                                } else if (!emailInput.contains("@") || !emailInput.contains(".")) {
                                    emailError = "Please enter a valid email address."
                                    valid = false
                                }

                                if (valid) {
                                    onSubmitRegistration(nameInput, rollInput, emailInput, phoneInput)
                                }
                            }
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (registrationState.isSubmitting) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Processing...",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = if (isFull) "Registration Closed (Event Full)" else "Confirm Registration",
                                color = if (isFull) FestTextMuted else Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
private fun InputField(
    label: String,
    isRequired: Boolean,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String?,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Row {
            Text(
                text = label,
                color = FestTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            if (isRequired) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "*",
                    color = FestRose,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF020617)) // bg-slate-950
                .border(
                    1.dp,
                    if (errorMessage != null) FestRose else FestBorder,
                    RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = FestTextMuted.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 12.sp
                ),
                cursorBrush = SolidColor(FestCyan),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = errorMessage,
                color = FestRose,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
