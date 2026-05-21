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
    primary = GeoPurplePrimaryDark,
    secondary = GeoPurpleSecondaryDark,
    tertiary = GeoPurpleTertiaryDark,
    background = GeoBgDark,
    surface = GeoSurfaceDark,
    surfaceVariant = GeoSurfaceVariantDark,
    primaryContainer = GeoContainerDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = GeoBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = GeoPurplePrimary,
    secondary = GeoPurpleSecondary,
    tertiary = GeoPurpleTertiary,
    background = GeoBgLight,
    surface = GeoSurfaceLight,
    surfaceVariant = GeoSurfaceVariantLight,
    primaryContainer = GeoContainerLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1D1B20),
    onSurface = Color(0xFF1D1B20),
    onSurfaceVariant = Color(0xFF49454F),
    outline = GeoBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Allow dynamic colors on Android 12+ if requested
    dynamicColor: Boolean = false, // Force custom theme to preserve Geometric Balance branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme // Dynamic and elegant light geometric theme as principal
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
