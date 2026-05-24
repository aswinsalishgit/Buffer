package com.aswinsalish.buffer.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape

private val BufferColorScheme = darkColorScheme(
    primary = AccentColor,
    onPrimary = TextColorPrimary,
    background = BackgroundColor,
    onBackground = TextColorPrimary,
    surface = BackgroundColor,
    onSurface = TextColorPrimary,
    surfaceVariant = BackgroundColor,
    onSurfaceVariant = TextColorSecondary
)

@Composable
fun BufferTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BufferColorScheme,
        typography = FuturisticTypography,
        shapes = androidx.compose.material3.Shapes(
            small = RectangleShape,
            medium = RectangleShape,
            large = RectangleShape
        ),
        content = content
    )
}
