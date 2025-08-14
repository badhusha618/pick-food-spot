package com.pickfoodplace.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Typography

private val DarkColorPalette = Colors(
    primary = Color(0xFFFF6F61),
    primaryVariant = Color(0xFFE25F53),
    secondary = Color(0xFFFF6F61),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

private val WearTypography = Typography()

@Composable
fun PickFoodPlaceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = WearTypography,
        content = content
    )
}