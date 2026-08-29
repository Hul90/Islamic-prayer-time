package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = IslamicGold,
    onPrimary = Color(0xFF17130A),
    primaryContainer = EmeraldContainerDark,
    onPrimaryContainer = OnEmeraldContainerDark,
    secondary = IslamicGoldLight,
    onSecondary = Color(0xFF17130A),
    tertiary = IslamicAccentCyan,
    background = IslamicBackgroundDark,
    surface = IslamicSurfaceDark,
    surfaceVariant = IslamicCardDark,
    onBackground = IslamicTextLight,
    onSurface = IslamicTextLight,
    onSurfaceVariant = IslamicMutedText,
    outline = Color(0xFF42564B)
)

private val LightColorScheme = lightColorScheme(
    primary = IslamicEmeraldDark,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainerLight,
    onPrimaryContainer = OnEmeraldContainerLight,
    secondary = IslamicGoldDark,
    onSecondary = Color(0xFF17130A),
    tertiary = IslamicAccentGreen,
    background = IslamicBackgroundLight,
    surface = IslamicSurfaceLight,
    surfaceVariant = IslamicCardLight,
    onBackground = IslamicTextDark,
    onSurface = IslamicTextDark,
    onSurfaceVariant = Color(0xFF58675E),
    outline = Color(0xFFD0DCD5)
)

@Composable
fun IslamicPrayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
