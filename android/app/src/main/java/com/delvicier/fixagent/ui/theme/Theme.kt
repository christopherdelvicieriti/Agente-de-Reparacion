package com.delvicier.fixagent.ui.theme

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
    primary = Dark_Primary,
    secondary = Dark_Primary,
    onPrimary = Dark_OnPrimary,
    inverseSurface = Dark_Tertiary,
    inverseOnSurface = Dark_OnTertiary,
    background = Dark_Background,
    onBackground = Dark_OnBackground,
    surface = Dark_Surface,
    onSurface = Dark_OnSurface,
    surfaceVariant = Dark_SecondaryContainer,
    onSurfaceVariant = Color(0xFFCAC4D0),
    secondaryContainer = Dark_SecondaryContainer,
    onSecondaryContainer = Color.White,
    inversePrimary = Dark_inversePrimary,
    onSecondary = Dark_OnSecondary,
    outline = Dark_Border,
    outlineVariant = Dark_OnBorder,
    surfaceContainerHighest = Dark_OnBorder,
)

private val LightColorScheme = lightColorScheme(
    primary = Light_Primary,
    secondary = Light_Primary,
    onPrimary = Light_OnPrimary,
    inverseSurface = Light_Tertiary,
    inverseOnSurface = Light_OnTertiary,
    background = Light_Background,
    onBackground = Light_OnBackground,
    surface = Light_Surface,
    onSurface = Light_OnSurface,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF49454F),
    secondaryContainer = Light_SecondaryContainer,
    onSecondaryContainer = Light_OnBackground,
    inversePrimary = Light_inversePrimary,
    onSecondary = Light_OnSecondary,
    outline = Light_Border,
    outlineVariant = Light_OnBorder,
    surfaceContainerHighest = Light_OnBorder,
)

@Composable
fun FixAgentTheme(
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = Color.Transparent.toArgb()

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}