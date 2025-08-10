package com.example.agriwork.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF047857),            // Deep earthy green
    onPrimary = Color.White,

    primaryContainer = Color(0xFF5E936C),   // Softer muted green
    onPrimaryContainer = Color.White,

    secondary = Color(0xFFD4A373),          // Warm wheat/golden brown
    onSecondary = Color.White,

    secondaryContainer = Color(0xFFF1D9B5), // Pale wheat tone
    onSecondaryContainer = Color(0xFF3E2E1E),

    background = Color(0xFFF7F6F3),         // Soft off-white with warm tint
    onBackground = Color(0xFF2E3B2C),       // Deep natural green-gray

    surface = Color(0xFFF7F6F3),            // Matches background for consistency
    onSurface = Color(0xff44403c),

    surfaceVariant = Color(0xFFE8F5E9),     // Light leafy green for cards
    onSurfaceVariant = Color(0xFF2E3B2C),

    error = Color(0xFFD32F2F),
    onError = Color.White
)


@Composable
fun AgriWorkTheme(
    darkTheme: Boolean = false, // Force Light Mode
    dynamicColor: Boolean = false, // Disable dynamic theme for consistency
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
