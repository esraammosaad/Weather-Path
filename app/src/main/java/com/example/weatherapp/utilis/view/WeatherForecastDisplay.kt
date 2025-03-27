package com.example.weatherapp.utilis.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.utilis.getWeatherGradient

@Composable
fun WeatherForecastDisplay(
    fiveDaysWeatherForecast: Response,
    icon: String
) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 32.dp)

    ) {
        when (fiveDaysWeatherForecast) {
            is Response.Failure -> {
                item {
                    Text(fiveDaysWeatherForecast.exception)
                }
            }

            Response.Loading -> {
                item {

                }

            }

            is Response.Success<*> -> {
                fiveDaysWeatherForecast as Response.Success<List<WeatherItem>>
                fiveDaysWeatherForecast.result?.let {

                    items(it.size) { index: Int ->
                        WeatherForecastItem(it[index], icon)
                    }

                }

            }
        }

    }

}