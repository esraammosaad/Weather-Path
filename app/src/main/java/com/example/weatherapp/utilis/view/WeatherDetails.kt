package com.example.weatherapp.utilis.view

import android.location.Address
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem

@Composable
fun WeatherDetails(
    countryName: Address?,
    selectedWeather: CurrentWeatherResponse?,
    fiveDaysWeatherForecastUiState: Response,
    listOfDays: List<List<WeatherItem>>
) {
    ImageDisplay()
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        CustomText(text = "TODAY")
        Spacer(modifier = Modifier.height(3.dp))
        countryName?.let {
            selectedWeather?.let { it1 ->
                LocationDisplay(
                    it,
                    it1
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        CurrentDateDisplay()
        Spacer(modifier = Modifier.height(5.dp))
        selectedWeather?.let { TemperatureDisplay(it) }
        selectedWeather?.let { WeatherStatusDisplay(it) }
        WeatherForecastDisplay(
            fiveDaysWeatherForecastUiState,
            selectedWeather?.weather?.firstOrNull()?.icon
                ?: ""
        )
        selectedWeather?.let {
            MoreDetailsContainer(
                it
            )
        }

        FiveDaysWeatherForecastDisplay(
            fiveDaysWeatherForecast = listOfDays
        )
    }
}