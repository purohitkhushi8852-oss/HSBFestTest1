package com.zenith26.fest.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.FestBorder
import com.zenith26.fest.ui.theme.FestCyan
import com.zenith26.fest.ui.theme.FestEmerald
import com.zenith26.fest.ui.theme.FestFuchsia
import com.zenith26.fest.ui.theme.FestSurface
import com.zenith26.fest.ui.theme.FestTextMuted
import com.zenith26.fest.ui.theme.FestTextPrimary
import com.zenith26.fest.ui.theme.FestTextSecondary
import com.zenith26.fest.ui.theme.FestViolet
import com.zenith26.fest.ui.theme.HeroTitleGradient

@Composable
fun HeroSection(
    totalEventsCount: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = FestBorder.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Date & Campus Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(FestSurface.copy(alpha = 0.9f))
                .border(1.dp, FestBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "📅 November 14 – 15, 2026",
                    color = FestCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "•",
                    color = FestTextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "📍 JKLU Campus",
                    color = Color(0xFFC4B5FD),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Headline
        Text(
            text = "Ignite Your Spark at",
            color = FestTextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )

        Text(
            text = "Zenith '26",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            style = androidx.compose.ui.text.TextStyle(brush = HeroTitleGradient)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Subtitle
        Text(
            text = "The premier college fest experience. Discover competitive hackathons, battle of the bands, robotics, e-sports, and creator workshops. Explore events and reserve your seats instantly.",
            color = FestTextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 4 Highlights Strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(FestSurface.copy(alpha = 0.85f))
                .border(1.dp, FestBorder, RoundedCornerShape(16.dp))
                .padding(vertical = 12.dp, horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stat 1: Flagship Events
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (totalEventsCount > 0) totalEventsCount.toString() else "14",
                        color = FestCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Flagship Events",
                        color = FestTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Stat 2: 2 Days
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "2 Days",
                        color = FestViolet,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Non-Stop Energy",
                        color = FestTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Stat 3: 5 Categories
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "5",
                        color = FestFuchsia,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Categories",
                        color = FestTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Stat 4: Live Instant Reg
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(FestEmerald)
                                .alpha(pulseAlpha)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Live",
                            color = FestEmerald,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Text(
                        text = "Instant Reg",
                        color = FestTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
