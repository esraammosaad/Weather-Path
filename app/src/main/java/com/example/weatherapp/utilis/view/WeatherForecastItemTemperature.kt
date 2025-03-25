package com.example.weatherapp.utilis.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.Strings

@Composable
fun WeatherForecastItemTemperature(weatherItem: WeatherItem) {
    Row {
        Text(
            weatherItem.main.temp.toString(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            fontFamily = poppinsFontFamily,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            Strings.CELSIUS_SYMBOL,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            fontFamily = poppinsFontFamily,
            color = Color.White
        )
    }
}