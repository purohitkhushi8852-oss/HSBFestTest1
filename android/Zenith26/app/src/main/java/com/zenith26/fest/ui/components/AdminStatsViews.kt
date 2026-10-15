package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.data.model.Stats
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
import com.zenith26.fest.ui.theme.FestBorder
import com.zenith26.fest.ui.theme.FestCyan
import com.zenith26.fest.ui.theme.FestEmerald
import com.zenith26.fest.ui.theme.FestSurface
import com.zenith26.fest.ui.theme.FestSurfaceLight
import com.zenith26.fest.ui.theme.FestTextMuted
import com.zenith26.fest.ui.theme.FestTextPrimary
import com.zenith26.fest.ui.theme.FestTextSecondary
import com.zenith26.fest.ui.theme.FestViolet

@Composable
fun AdminStatCards(
    stats: Stats?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: Total Events & Total Registrations
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "TOTAL EVENTS",
                value = stats?.totalEvents?.toString() ?: "--",
                subtitle = "Configured in fest",
                icon = "🎯",
                valueColor = FestCyan,
                iconBg = Color(0xCC083344),
                iconBorder = Color(0x6606B6D4),
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "REGISTRATIONS",
                value = stats?.totalRegistrations?.toString() ?: "--",
                subtitle = "Unique student signups",
                icon = "🎟️",
                valueColor = FestViolet,
                iconBg = Color(0xCC2E1065),
                iconBorder = Color(0x668B5CF6),
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: Capacity Utilization & Upcoming Events
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "CAPACITY UTILIZATION",
                value = if (stats != null) "${stats.overallUtilizationPct}%" else "--%",
                subtitle = "Overall attendance rate",
                icon = "📈",
                valueColor = FestEmerald,
                iconBg = Color(0xCC064E3B),
                iconBorder = Color(0x6610B981),
                progressPct = stats?.overallUtilizationPct?.toFloat()?.coerceIn(0f, 100f),
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "UPCOMING EVENTS",
                value = stats?.upcomingEventsCount?.toString() ?: "--",
                subtitle = "Scheduled today or later",
                icon = "⏳",
                valueColor = FestAmber,
                iconBg = Color(0xCC451A03),
                iconBorder = Color(0x66F59E0B),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: String,
    valueColor: Color,
    iconBg: Color,
    iconBorder: Color,
    progressPct: Float? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(FestSurface.copy(alpha = 0.9f))
            .border(1.dp, FestBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = FestTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = value,
                    color = valueColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )

                if (progressPct != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(FestSurfaceLight)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressPct / 100f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(FestEmerald, FestCyan)
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    color = FestTextMuted.copy(alpha = 0.8f),
                    fontSize = 9.sp,
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg)
                    .border(1.dp, iconBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun CategoryBreakdownView(
    categoryCounts: Map<String, Int>?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FestSurface.copy(alpha = 0.9f))
            .border(1.dp, FestBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "EVENT DISTRIBUTION BY CATEGORY",
                color = FestTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (categoryCounts.isNullOrEmpty()) {
                Text(text = "No categories loaded.", color = FestTextMuted, fontSize = 11.sp)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryCounts.entries.forEach { entry ->
                        val (catBg, catText, catBorder) = when (entry.key.lowercase()) {
                            "technical" -> Triple(CatTechnicalBg, CatTechnicalText, CatTechnicalBorder)
                            "cultural" -> Triple(CatCulturalBg, CatCulturalText, CatCulturalBorder)
                            "sports" -> Triple(CatSportsBg, CatSportsText, CatSportsBorder)
                            "gaming" -> Triple(CatGamingBg, CatGamingText, CatGamingBorder)
                            "workshop" -> Triple(CatWorkshopBg, CatWorkshopText, CatWorkshopBorder)
                            else -> Triple(FestSurfaceLight, FestTextSecondary, FestBorder)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(catBg)
                                .border(1.dp, catBorder, RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = entry.key.uppercase(),
                                    color = catText,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = entry.value.toString(),
                                        color = FestTextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "ev",
                                        color = FestTextMuted,
                                        fontSize = 8.sp,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
