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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.BrandTextGradient
import com.zenith26.fest.ui.theme.CatTechnicalBg
import com.zenith26.fest.ui.theme.CatTechnicalBorder
import com.zenith26.fest.ui.theme.CatTechnicalText
import com.zenith26.fest.ui.theme.FestBorder
import com.zenith26.fest.ui.theme.FestSurface
import com.zenith26.fest.ui.theme.FestSurfaceLight
import com.zenith26.fest.ui.theme.FestTextMuted
import com.zenith26.fest.ui.theme.FestTextPrimary
import com.zenith26.fest.ui.theme.FestViolet
import com.zenith26.fest.ui.theme.PrimaryButtonGradient

@Composable
fun ZenithNavbar(
    isAdminMode: Boolean,
    onToggleMode: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xE60C1222))
            .border(width = 1.dp, color = FestBorder.copy(alpha = 0.8f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Titles
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { if (isAdminMode) onToggleMode() }
            ) {
                // Gradient Squircle ⚡ Logo
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryButtonGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isAdminMode) "📊" else "⚡",
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Zenith '26",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            style = androidx.compose.ui.text.TextStyle(brush = BrandTextGradient)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Annual Fest or Coordinator badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAdminMode) Color(0xCC2E1065) else CatTechnicalBg)
                                .border(
                                    1.dp,
                                    if (isAdminMode) Color(0x668B5CF6) else CatTechnicalBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isAdminMode) "CONSOLE" else "ANNUAL FEST",
                                color = if (isAdminMode) Color(0xFFC4B5FD) else CatTechnicalText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Text(
                        text = if (isAdminMode) "Operations & Capacity Management" else "Campus Event Management System",
                        color = FestTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Right Actions: Toggle Portal/Console & Settings
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Settings button for API Base URL configuration
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "API Settings",
                        tint = FestTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Toggle Button (Coordinator Console / Student Portal)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(FestSurface)
                        .border(1.dp, FestBorder, RoundedCornerShape(10.dp))
                        .clickable { onToggleMode() }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isAdminMode) Icons.Default.Home else Icons.Default.BarChart,
                            contentDescription = null,
                            tint = if (isAdminMode) Color(0xFF38BDF8) else FestViolet,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isAdminMode) "Student" else "Console",
                            color = FestTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
