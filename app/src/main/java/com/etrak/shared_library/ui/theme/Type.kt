package com.etrak.shared_library.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.etrak.shared_library.R

// Set of Material typography styles to start with
val Typography = Typography(
    defaultFontFamily = FontFamily(Font(R.font.roboto_black)),
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    h1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 42.sp
    ),
    button = TextStyle(
        fontWeight = FontWeight.Bold
    )

    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

val terminalTextStyle = TextStyle(
    fontFamily = FontFamily(
        Font(R.font.terminal)
    ),
    fontSize = 16.sp,
)

val dialogTitleTextStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 36.sp,
)

