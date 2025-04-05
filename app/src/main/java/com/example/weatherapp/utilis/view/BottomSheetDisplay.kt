package com.example.weatherapp.utilis.view

import android.location.Address
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.google.maps.android.compose.MarkerState
import java.math.RoundingMode

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomSheetDisplay(
    currentWeatherUiState: Response,
    markerState: MarkerState,
    fiveDaysWeatherForecast: Response,
    countryName: Response,
    showBottomSheet: MutableState<Boolean>,
    listOfDays: List<List<WeatherItem>>,
    isConnected: Boolean,
    text:Int,
    onAddClicked: (CurrentWeatherResponse, FiveDaysWeatherForecastResponse) -> Unit
) {

    val selectedWeather: CurrentWeatherResponse?
    val selectedFiveDaysForecast: FiveDaysWeatherForecastResponse?

    when (currentWeatherUiState) {
        is Response.Loading -> {
            Loading()
        }
        is Response.Failure -> {
            Failure(currentWeatherUiState, isConnected)
        }
        is Response.Success<*> -> {
            currentWeatherUiState as Response.Success<CurrentWeatherResponse>
            LaunchedEffect(currentWeatherUiState.result) {
                markerState.showInfoWindow()
            }
            selectedWeather = currentWeatherUiState.result
            selectedWeather?.latitude =
                markerState.position.latitude.toBigDecimal().setScale(2, RoundingMode.DOWN)
                    .toDouble()
            selectedWeather?.longitude =
                markerState.position.longitude.toBigDecimal().setScale(2, RoundingMode.DOWN)
                    .toDouble()
            when (fiveDaysWeatherForecast) {
                is Response.Loading -> Loading()
                is Response.Failure -> Failure(fiveDaysWeatherForecast, isConnected)
                is Response.Success<*> -> {
                    fiveDaysWeatherForecast as Response.Success<List<WeatherItem>>
                    selectedFiveDaysForecast = fiveDaysWeatherForecast.result?.let {
                        FiveDaysWeatherForecastResponse(
                            it,
                            longitude = markerState.position.longitude.toBigDecimal()
                                .setScale(2, RoundingMode.DOWN)
                                .toDouble(),
                            latitude = markerState.position.latitude.toBigDecimal()
                                .setScale(2, RoundingMode.DOWN)
                                .toDouble()
                        )
                    }
                    when (countryName) {
                        is Response.Failure -> {
                            Text(countryName.exception)
                        }
                        Response.Loading -> {
                            Text(stringResource(R.string.n_a))
                        }
                        is Response.Success<*> -> {
                            countryName as Response.Success<Address>
                            selectedWeather?.countryName = countryName.result?.countryName ?: ""
                            selectedWeather?.cityName = countryName.result?.locality ?: ""
                            PartialBottomSheet(
                                showBottomSheet,
                                selectedWeather,
                                countryName,
                                fiveDaysWeatherForecast,
                                listOfDays,
                                text
                            ) {
                                if (selectedWeather != null && selectedFiveDaysForecast != null) {
                                    onAddClicked.invoke(selectedWeather, selectedFiveDaysForecast)
                                }

                            }
                        }
                    }


                }
            }
        }
    }
}




