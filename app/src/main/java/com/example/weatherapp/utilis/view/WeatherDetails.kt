package com.example.weatherapp.utilis.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.home.view.CustomWeatherDetails

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherDetails(
    countryName: Response,
    selectedWeather: CurrentWeatherResponse?,
    fiveDaysWeatherForecastUiState: Response,
    listOfDays: List<List<WeatherItem>>
) {

    val context = LocalContext.current
    if (selectedWeather != null) {
        ImageDisplay(selectedWeather)
    }
    if (selectedWeather != null) {
        CustomWeatherDetails(
            currentWeather = selectedWeather,
            countryName = countryName,
            context = context,
            fiveDaysWeatherForecast = fiveDaysWeatherForecastUiState,
            listOfDays = listOfDays
        )
    }

//    Column(
//        modifier = Modifier
//            .fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//
//    ) {
//        CustomText(text = stringResource(R.string.today))
//        Spacer(modifier = Modifier.height(3.dp))
//        countryName?.let {
//            selectedWeather?.let { it1 ->
//                LocationDisplay(
//                    it,
//                    it1
//                )
//            }
//        }
//        Spacer(modifier = Modifier.height(5.dp))
//        CurrentDateDisplay()
//        Spacer(modifier = Modifier.height(5.dp))
//        selectedWeather?.let { TemperatureDisplay(it) }
//        selectedWeather?.let { WeatherStatusDisplay(it) }
//        WeatherForecastDisplay(
//            fiveDaysWeatherForecastUiState,
//            selectedWeather?.weather?.firstOrNull()?.icon
//                ?: ""
//        )
//        selectedWeather?.let {
//            MoreDetailsContainer(
//                it
//            )
//        }
//
//        FiveDaysWeatherForecastDisplay(
//            fiveDaysWeatherForecast = listOfDays
//        )
//    }
}