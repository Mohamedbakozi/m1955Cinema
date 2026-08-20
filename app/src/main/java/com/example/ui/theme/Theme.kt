package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = CinemaRed,
    onPrimary = Color.White,
    primaryContainer = CinemaRedDark,
    onPrimaryContainer = Color.White,
    secondary = CinemaGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3B2F08),
    onSecondaryContainer = CinemaGold,
    tertiary = CinemaCyan,
    onTertiary = Color.Black,
    background = CinemaDarkBg,
    onBackground = CinemaTextPrimary,
    surface = CinemaDarkSurface,
    onSurface = CinemaTextPrimary,
    surfaceVariant = CinemaDarkSurfaceVariant,
    onSurfaceVariant = CinemaTextSecondary,
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CinemaRed,
    onPrimary = Color.White,
    secondary = CinemaGold,
    onSecondary = Color.Black,
    tertiary = CinemaCyan,
    background = CinemaLightBg,
    onBackground = Color(0xFF0F172A),
    surface = CinemaLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = CinemaLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569)
  )

@Composable
fun M1955CinemaTheme(
  darkTheme: Boolean = true, // Default to sleek immersive cinema dark theme
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun KurdCinemaTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit,
) {
  M1955CinemaTheme(darkTheme = darkTheme, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  M1955CinemaTheme(darkTheme = darkTheme, content = content)
}

