package com.example.weatherapp.utilis.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.getCurrentDate

@Composable
fun CurrentDateDisplay() {
    Text(
        text = getCurrentDate(0),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        fontFamily = poppinsFontFamily,
        color = Color.White
    )
}