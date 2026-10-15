package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.FestCyan
import com.zenith26.fest.ui.theme.FestSurfaceLight
import com.zenith26.fest.ui.theme.FestTextPrimary
import com.zenith26.fest.ui.theme.FestTextSecondary
import com.zenith26.fest.ui.theme.FestViolet

val CategoryList = listOf(
    "All",
    "Technical",
    "Cultural",
    "Sports",
    "Gaming",
    "Workshop"
)

private val ActiveFilterGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF06B6D4), // Cyan-500
        Color(0xFF8B5CF6)  // Violet-600
    )
)

@Composable
fun CategoryFilterBar(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryList.forEach { category ->
            val isSelected = category.equals(selectedCategory, ignoreCase = true)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .then(
                        if (isSelected) {
                            Modifier
                                .background(ActiveFilterGradient)
                                .border(1.dp, Color(0x6638BDF8), RoundedCornerShape(12.dp))
                        } else {
                            Modifier
                                .background(FestSurfaceLight.copy(alpha = 0.8f))
                                .border(1.dp, Color(0x66334155), RoundedCornerShape(12.dp))
                        }
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
                    color = if (isSelected) FestTextPrimary else FestTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
