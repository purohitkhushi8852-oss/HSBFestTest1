package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zenith26.fest.ui.theme.FestBorder
import com.zenith26.fest.ui.theme.FestCyan
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

@Composable
fun AddEventModal(
    isOpen: Boolean,
    isSubmitting: Boolean,
    alert: Pair<AlertType, String>?,
    onDismiss: () -> Unit,
    onSubmit: (
        title: String,
        category: String,
        icon: String,
        date: String,
        time: String,
        venue: String,
        capacity: Int,
        fee: Int,
        description: String
    ) -> Unit
) {
    if (!isOpen) return

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
            var title by remember { mutableStateOf("") }
            var category by remember { mutableStateOf("Technical") }
            var icon by remember { mutableStateOf("⚡") }
            var date by remember { mutableStateOf("2026-11-14") }
            var time by remember { mutableStateOf("10:00 AM") }
            var venue by remember { mutableStateOf("") }
            var capacityStr by remember { mutableStateOf("50") }
            var feeStr by remember { mutableStateOf("0") }
            var description by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ModalHeaderGradient)
                        .border(1.dp, FestBorder.copy(alpha = 0.8f))
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Create New Fest Event",
                        color = FestTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(FestSurfaceLight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = FestTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(18.dp)) {
                    // Alert
                    if (alert != null) {
                        val isSuccess = alert.first == AlertType.SUCCESS
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSuccess) FestEmeraldBg else FestRoseBg)
                                .border(1.dp, if (isSuccess) Color(0x6610B981) else Color(0x66F43F5E), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = alert.second,
                                color = if (isSuccess) FestEmeraldText else FestRoseText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Form Fields
                    AddInputField("Event Title *", title) { title = it }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            AddInputField("Category *", category) { category = it }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            AddInputField("Icon (Emoji)", icon) { icon = it }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            AddInputField("Date *", date) { date = it }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            AddInputField("Time *", time) { time = it }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AddInputField("Venue *", venue) { venue = it }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            AddInputField("Capacity *", capacityStr, KeyboardType.Number) { capacityStr = it }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            AddInputField("Fee (₹)", feeStr, KeyboardType.Number) { feeStr = it }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AddInputField("Description", description) { description = it }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryButtonGradient)
                            .clickable(enabled = !isSubmitting) {
                                val cap = capacityStr.toIntOrNull() ?: 50
                                val fee = feeStr.toIntOrNull() ?: 0
                                onSubmit(title, category, icon, date, time, venue, cap, fee, description)
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "Save Event",
                                color = Color.White,
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

@Composable
private fun AddInputField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(text = label, color = FestTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF020617))
                .border(1.dp, FestBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                cursorBrush = SolidColor(FestCyan),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
