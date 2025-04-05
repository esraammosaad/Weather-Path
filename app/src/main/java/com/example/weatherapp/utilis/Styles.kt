package com.example.weatherapp.utilis

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.ui.theme.poppinsFontFamily


class Styles {
    companion object {
        val textStyleSemiBold26 = TextStyle(
            fontSize = 26.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontFamily = poppinsFontFamily,
        )
        val textStyleMedium15 = TextStyle(
            fontWeight = FontWeight.Medium,
            fontFamily = poppinsFontFamily,
            fontSize = 15.sp
        )
        val textStyleMedium16 = TextStyle(
            fontWeight = FontWeight.Medium,
            fontFamily = poppinsFontFamily,
            fontSize = 16.sp
        )

        val textStyleSemiBold18 = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontFamily = poppinsFontFamily,
            fontSize = 18.sp,
        )

        val textStyleSemiBold20 = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontFamily = poppinsFontFamily,
            fontSize = 20.sp,
        )
        val textStyleSemiBold22 = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontFamily = poppinsFontFamily,
            fontSize = 22.sp,
        )
        val textStyleSemiBold15 = TextStyle(
            fontSize = 15.sp,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.SemiBold,
        )
        val textStyleBold16 = TextStyle(
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = poppinsFontFamily,
            fontSize = 16.sp
        )

        val textStyleNormal16 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontFamily = poppinsFontFamily,
            fontSize = 16.sp
        )

        val textStyleNormal14 = TextStyle(
            fontSize = 14.sp,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Normal,
            color = PrimaryColor,
        )
        val textStyleMedium30 = TextStyle(
            fontWeight = FontWeight.Medium,
            fontFamily = poppinsFontFamily,
            fontSize = 30.sp
        )

        val textStyleMedium28 = TextStyle(
            fontWeight = FontWeight.Medium,
            fontFamily = poppinsFontFamily,
            fontSize = 28.sp
        )
        val textStyleMedium18 = TextStyle(
            fontWeight = FontWeight.Medium,
            fontFamily = poppinsFontFamily,
            fontSize = 18.sp
        )
        val textStyleMedium17 = TextStyle(
            fontWeight = FontWeight.Medium,
            fontFamily = poppinsFontFamily,
            fontSize = 17.sp
        )

    }
}