package com.example.weatherapp.utilis.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.poppinsFontFamily

@Composable
fun WeatherStatusDisplay(currentWeather: CurrentWeatherResponse) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(start = 12.dp)
    ) {
        Text(
            text = currentWeather.weather[0].description,
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            fontFamily = poppinsFontFamily,
            color = OffWhite
        )
        WeatherStatusImageDisplay(
            currentWeather.weather[0].icon
        )
    }
}
