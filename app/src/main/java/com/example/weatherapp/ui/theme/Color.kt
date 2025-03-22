package com.example.weatherapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val OffWhite = Color(0xFFF5F5F4)

val PrimaryColor = Color(0xFF274170)

val NightColor = Color(0xff26375a)

val DayColor = Color(0xff3b6d9d)

val DayTheme = Brush.linearGradient(
    colors = listOf(
        Color(0xFF6EC6FF),
        Color(0xFF2196F3),
        Color(0xFF64B5F6),
        Color(0xFFFFD54F)
    )
)

val NightTheme = Brush.linearGradient(
    colors = listOf(
        Color(0xFF1B2A49),
        Color(0xFF16213E),
        Color(0xFF0F3460),
        Color(0xFF274170)
    )
)