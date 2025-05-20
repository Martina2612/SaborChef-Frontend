package com.example.saborchef.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


// Tu paleta de colores (reutilizable)
val BackgroundLight = Color(0xFFF6F6F6)
val BackgroundDark  = Color(0xFF0E0E0E)
val SurfaceLight     = Color(0xFFFFFFFF)
val SurfaceDark      = Color(0xFF1C1B1F)
val GrayText         = Color(0xFFCBCBCB)

private val DarkColorScheme = darkColorScheme(
    primary      = OrangeDark,
    secondary    = BlueDark,
    tertiary     = Orange,
    background   = BackgroundDark,
    surface      = SurfaceDark,
    onPrimary    = Color.White,
    onSecondary  = Color.White,
    onTertiary   = Color.Black,
    onBackground = Color.White,
    onSurface    = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary      = Orange,
    secondary    = Blue,
    tertiary     = OrangeDark,
    background   = BackgroundLight,
    surface      = SurfaceLight,
    onPrimary    = Color.White,
    onSecondary  = Color.White,
    onTertiary   = Color.White,
    onBackground = Color.Black,
    onSurface    = Color.Black
)

@Composable
fun SaborChefTheme(
    content: @Composable () -> Unit
) {
    // por ahora solo light
    MaterialTheme(
        colorScheme  = LightColorScheme,
        typography   = Typography,
        content      = content
    )
}
