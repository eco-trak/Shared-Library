package com.etrak.shared_library.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)


/**
 * McConsole
 */
val terminalFontColor = Color(0xFF4AF626)


/**
 * Background
 */
var darkBackgroundColor1 = Color(0xFF0A0A0A)
var darkBackgroundColor2 = Color(0xFF1E1E1E)
var lightBackgroundColor1 = Color(0xFFE4E4E4)
var lightBackgroundColor2 = Color(0xFFC2C2C2)
val darkBackgroundGradient = Brush.linearGradient(
    colors = listOf(
        darkBackgroundColor1,
        darkBackgroundColor2
    )
)
val lightBackgroundGradient = Brush.linearGradient(
    colors = listOf(
        lightBackgroundColor1,
        lightBackgroundColor2
    )
)

/**
 * Button
 */
val darkButtonBackgroundColor1 = Color(0x99404040)
val darkButtonBackgroundColor2 = Color(0x60404040)
val darkButtonBackgroundGradient = Brush.linearGradient(
    colors = listOf(
        darkButtonBackgroundColor1,
        darkButtonBackgroundColor2
    )
)
val lightButtonBackgroundColor1 = Color(0x99404040)
val lightButtonBackgroundColor2 = Color(0x60404040)
val lightButtonBackgroundGradient = Brush.linearGradient(
    colors = listOf(
        lightButtonBackgroundColor1,
        lightButtonBackgroundColor2
    )
)
val darkButtonBorderColor1 = Color(0xFF535353)
val darkButtonBorderColor2 = Color(0xA0535353)
val darkButtonBorderGradient = Brush.linearGradient(
    colors = listOf(
        darkButtonBorderColor1,
        darkButtonBorderColor2
    )
)
val lightButtonBorderColor1 = Color(0xFF535353)
val lightButtonBorderColor2 = Color(0xA0535353)
val lightButtonBorderGradient = Brush.linearGradient(
    colors = listOf(
        lightButtonBorderColor1,
        lightButtonBorderColor2
    )
)
val darkButtonDividerColor1 = Color(0xFF0E5492)
val darkButtonDividerColor2 = Color(0xFF002260)
val darkButtonDividerGradient = Brush.linearGradient(
    colors = listOf(
        darkButtonDividerColor1,
        darkButtonDividerColor2
    )
)
val lightButtonDividerColor1 = Color(0xFF0E5492)
val lightButtonDividerColor2 = Color(0xFF002260)
val lightButtonDividerGradient = Brush.linearGradient(
    colors = listOf(
        lightButtonDividerColor1,
        lightButtonDividerColor2
    )
)
val darkBadgeBackgroundColor = Color(0xFF04B204)
val darkBadgeContentColor = Color(0xFF1A1A1A)
val lightBadgeBackgroundColor = Color(0xFF04B204)
val lightBadgeContentColor = Color(0xFF1A1A1A)

