package com.example.weatherapp.utilis.view

import android.location.Address
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.poppinsFontFamily

@Composable
fun LocationDisplay(countryName: Address, currentWeather: CurrentWeatherResponse) {
    Text(
        countryName.locality ?: currentWeather.name,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        fontFamily = poppinsFontFamily,
        color = Color.White,
        textAlign = TextAlign.Center

    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
        countryName.countryName ?: currentWeather.sys.country,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        fontFamily = poppinsFontFamily,
        color = OffWhite,
        textAlign = TextAlign.Center

    )

}