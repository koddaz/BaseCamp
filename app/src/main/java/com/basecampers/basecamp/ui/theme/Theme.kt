package com.basecampers.basecamp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryRed,
    onPrimary = AppBackground,
    primaryContainer = PrimaryRed.copy(alpha = 0.1f),
    onPrimaryContainer = PrimaryRed,
    
    secondary = SecondaryAqua,
    onSecondary = AppBackground,
    secondaryContainer = SecondaryAqua.copy(alpha = 0.1f),
    onSecondaryContainer = SecondaryAqua,
    
    background = AppBackground,
    onBackground = TextPrimary,
    
    surface = CardBackground,
    onSurface = TextPrimary,
    
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary,
    
    outline = BorderColor,
    outlineVariant = BorderColor.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryRed,
    onPrimary = AppBackground,
    primaryContainer = PrimaryRed.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryRed,
    
    secondary = SecondaryAqua,
    onSecondary = AppBackground,
    secondaryContainer = SecondaryAqua.copy(alpha = 0.2f),
    onSecondaryContainer = SecondaryAqua,
    
    background = TextPrimary,
    onBackground = AppBackground,
    
    surface = TextPrimary,
    onSurface = AppBackground,
    
    surfaceVariant = TextPrimary,
    onSurfaceVariant = AppBackground,
    
    outline = BorderColor,
    outlineVariant = BorderColor.copy(alpha = 0.5f)
)

@Composable
fun BaseCampTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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