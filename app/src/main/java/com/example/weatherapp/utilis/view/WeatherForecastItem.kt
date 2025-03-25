package com.example.weatherapp.utilis.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.utilis.formatTime
import com.example.weatherapp.utilis.getWeatherGradient

@Composable
fun WeatherForecastItem(weatherItem: WeatherItem, icon: String) {

    Column(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(100.dp)
            .border(
                width = 1.dp,
                brush = getWeatherGradient(icon),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomText(formatTime(weatherItem.dt_txt))
        WeatherStatusImageDisplay(
            weatherItem.weather[0].icon
        )
        WeatherForecastItemTemperature(weatherItem)
    }
}