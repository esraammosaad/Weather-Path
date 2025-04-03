package com.example.weatherapp.utilis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.utilis.Styles

@Composable
fun WeatherForecastDisplay(
    fiveDaysWeatherForecast: Response,
    icon: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 24.dp)
    ) {
        Text(
            stringResource(R.string._3_hourly_forecast),
            style = Styles.textStyleSemiBold22,
            color = Color.White

        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow {
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
}