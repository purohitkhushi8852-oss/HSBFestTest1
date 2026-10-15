package com.zenith26.fest.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val FestBg = Color(0xFF080D1A)
val FestSurface = Color(0xFF0F172A)
val FestSurfaceLight = Color(0xFF1E293B)
val FestSurfaceLighter = Color(0xFF334155)
val FestBorder = Color(0xFF334155)
val FestBorderSubtle = Color(0x40334155)
val FestElectric = Color(0xFF38BDF8)
val FestViolet = Color(0xFF8B5CF6)
val FestFuchsia = Color(0xFFD946EF)
val FestCyan = Color(0xFF06B6D4)

val FestTextPrimary = Color(0xFFF8FAFC)
val FestTextSecondary = Color(0xFFCBD5E1)
val FestTextMuted = Color(0xFF94A3B8)
val FestTextDim = Color(0xFF64748B)

val FestEmerald = Color(0xFF10B981)
val FestEmeraldBg = Color(0xCC064E3B)
val FestEmeraldText = Color(0xFF34D399)

val FestAmber = Color(0xFFF59E0B)
val FestAmberBg = Color(0xCC451A03)
val FestAmberText = Color(0xFFFBBF24)

val FestRose = Color(0xFFF43F5E)
val FestRoseBg = Color(0xCC4C0519)
val FestRoseText = Color(0xFFFDA4AF)

// Category Badges Dark-Neon
val CatTechnicalBg = Color(0xCC083344)
val CatTechnicalText = Color(0xFF67E8F9)
val CatTechnicalBorder = Color(0x6606B6D4)

val CatCulturalBg = Color(0xCC701A75)
val CatCulturalText = Color(0xFFF0ABFC)
val CatCulturalBorder = Color(0x66D946EF)

val CatSportsBg = Color(0xCC064E3B)
val CatSportsText = Color(0xFF6EE7B7)
val CatSportsBorder = Color(0x6610B981)

val CatGamingBg = Color(0xCC451A03)
val CatGamingText = Color(0xFFFCD34D)
val CatGamingBorder = Color(0x66F59E0B)

val CatWorkshopBg = Color(0xCC2E1065)
val CatWorkshopText = Color(0xFFC4B5FD)
val CatWorkshopBorder = Color(0x668B5CF6)

// Gradients
val PrimaryButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF06B6D4), // Cyan
        Color(0xFF8B5CF6), // Violet
        Color(0xFFD946EF)  // Fuchsia
    )
)

val BrandTextGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF38BDF8),
        Color(0xFFC4B5FD),
        Color(0xFFE879F9)
    )
)

val HeroTitleGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF22D3EE),
        Color(0xFFA78BFA),
        Color(0xFFF472B6)
    )
)

val ModalHeaderGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF2E1065),
        Color(0xFF312E81),
        Color(0xFF0F172A)
    )
)
