package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.CatTechnicalBg
import com.zenith26.fest.ui.theme.CatTechnicalBorder
import com.zenith26.fest.ui.theme.CatTechnicalText
import com.zenith26.fest.ui.theme.FestBorder
import com.zenith26.fest.ui.theme.FestCyan
import com.zenith26.fest.ui.theme.FestRose
import com.zenith26.fest.ui.theme.FestRoseBg
import com.zenith26.fest.ui.theme.FestRoseText
import com.zenith26.fest.ui.theme.FestSurface
import com.zenith26.fest.ui.theme.FestTextMuted
import com.zenith26.fest.ui.theme.FestTextPrimary

@Composable
fun LoadingStateView(
    message: String = "Fetching events from server...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = FestCyan,
            strokeWidth = 3.dp,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = message,
            color = FestTextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ErrorStateView(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(FestRoseBg.copy(alpha = 0.5f))
                .border(1.dp, Color(0x66F43F5E), RoundedCornerShape(18.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(FestRoseBg)
                        .border(1.dp, Color(0x66F43F5E), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚠️", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Failed to Load Events",
                    color = FestRoseText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = errorMessage,
                    color = FestRoseText.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(FestRose)
                        .clickable { onRetry() }
                        .padding(horizontal = 18.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = "Try Again",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(FestSurface)
                .border(1.dp, FestBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🔍", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "No events match your search",
            color = FestTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Try clearing your search query or picking a different category filter.",
            color = FestTextMuted,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(CatTechnicalBg)
                .border(1.dp, CatTechnicalBorder, RoundedCornerShape(10.dp))
                .clickable { onResetFilters() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Reset Filters",
                color = CatTechnicalText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
