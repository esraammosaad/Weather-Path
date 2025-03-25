package com.example.weatherapp.utilis.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.poppinsFontFamily

@Composable
fun CustomText(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        fontFamily = poppinsFontFamily,
        color = Color.White,

        )
}