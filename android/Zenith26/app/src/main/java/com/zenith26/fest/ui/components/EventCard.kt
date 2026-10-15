package com.zenith26.fest.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.data.model.Event
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
import com.zenith26.fest.ui.theme.PrimaryButtonGradient

@Composable
fun EventCard(
    event: Event,
    onViewDetails: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isFull = event.availableSeats == 0
    val isLowSeats = event.availableSeats in 1..10

    // Pulsing animation for low seats warning
    val infiniteTransition = rememberInfiniteTransition(label = "pulseLowSeats")
    val amberAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "amberAlpha"
    )

    // Category styling
    val (catBg, catText, catBorder) = when (event.category.lowercase()) {
        "technical" -> Triple(CatTechnicalBg, CatTechnicalText, CatTechnicalBorder)
        "cultural" -> Triple(CatCulturalBg, CatCulturalText, CatCulturalBorder)
        "sports" -> Triple(CatSportsBg, CatSportsText, CatSportsBorder)
        "gaming" -> Triple(CatGamingBg, CatGamingText, CatGamingBorder)
        "workshop" -> Triple(CatWorkshopBg, CatWorkshopText, CatWorkshopBorder)
        else -> Triple(FestSurfaceLight, FestTextSecondary, FestBorder)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FestSurface.copy(alpha = 0.9f))
            .border(1.dp, FestBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Icon + Category Badge + Seats Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Event Icon Container
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FestSurfaceLight)
                        .border(1.dp, FestBorder.copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (!event.icon.isNullOrBlank()) event.icon else "⚡",
                        fontSize = 22.sp
                    )
                }

                // Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(catBg)
                            .border(1.dp, catBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = event.category.uppercase(),
                            color = catText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Seats Badge
                    if (isFull) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(FestRoseBg)
                                .border(1.dp, Color(0x66F43F5E), RoundedCornerShape(20.dp))
                                .padding(horizontal = 9.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(FestRose)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "FULL",
                                    color = FestRoseText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else if (isLowSeats) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(FestAmberBg)
                                .border(1.dp, Color(0x66F59E0B), RoundedCornerShape(20.dp))
                                .padding(horizontal = 9.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(FestAmber)
                                        .alpha(amberAlpha)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${event.availableSeats} seats left",
                                    color = FestAmberText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(FestSurfaceLight.copy(alpha = 0.8f))
                                .border(1.dp, FestBorder.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 9.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${event.availableSeats} / ${event.capacity} seats",
                                color = FestTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = event.title,
                color = FestTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = if (!event.description.isNullOrBlank()) event.description else "Join us for an exciting fest experience.",
                color = FestTextMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Divider
            HorizontalDivider(color = FestBorder.copy(alpha = 0.6f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(10.dp))

            // Event Details Meta: Date, Venue, Fee
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📅", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${event.eventDate} • ${event.eventTime}",
                        color = FestTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📍", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.venue,
                        color = FestTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎟️", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (event.fee == 0) "Free Entry" else "₹${event.fee}",
                        color = if (event.fee == 0) FestEmerald else FestCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // View Details Button
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
                    .clickable { onViewDetails(event.id) }
                    .padding(vertical = 10.dp, horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isFull) "View Details (Full)" else "View Details & Register",
                        color = if (isFull) FestTextMuted else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = if (isFull) FestTextMuted else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
