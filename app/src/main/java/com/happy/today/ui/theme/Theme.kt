package com.happy.today.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HappyColorScheme = lightColorScheme(
    primary = HappyCoral,
    secondary = HappyYellow,
    tertiary = HappyGreen,
    background = HappyCream,
    surface = HappyCream,
    onBackground = HappyInk,
    onSurface = HappyInk
)

@Composable
fun HappyTodayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HappyColorScheme,
        typography = HappyTypography,
        content = content
    )
}
