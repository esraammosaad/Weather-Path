package com.example.weatherapp.favorite.view.screens

import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.FailureDisplay
import com.example.weatherapp.utilis.view.LoadingDisplay
import com.example.weatherapp.utilis.view.WeatherDetails
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherDetailsScreen(
    longitude: Double,
    latitude: Double,
    favoriteViewModel: FavoriteViewModelImpl,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel,
    isConnected : Boolean
) {

    val context = LocalContext.current
    val languageCode = LocalStorageDataSource.getInstance(context).getLanguageCode
    val tempUnit = LocalStorageDataSource.getInstance(context).getTempUnit

    LaunchedEffect(Unit) {
        favoriteViewModel.getSelectedWeather(
            longitude = longitude,
            latitude = latitude,
            languageCode = languageCode,
            tempUnit = tempUnit,
            isConnected = isConnected
        )
        favoriteViewModel.getSelectedFiveDaysWeatherForecast(
            longitude = longitude,
            latitude = latitude,
            languageCode = languageCode,
            tempUnit = tempUnit,
            isConnected = isConnected
        )
        favoriteViewModel.getCountryName(
            longitude = longitude, latitude = latitude, Geocoder(
                context,
                Locale(LocalStorageDataSource.getInstance(context).getLanguageCode)
            ), isConnected = isConnected
        )
    }

    val selectedWeather = favoriteViewModel.selectedWeather.collectAsStateWithLifecycle().value

    val selectedFiveDaysWeatherForecast =
        favoriteViewModel.selectedFiveDaysWeatherForecast.collectAsStateWithLifecycle().value
    val countryName = favoriteViewModel.countryName.collectAsStateWithLifecycle().value

    val currentDayForecast =
        favoriteViewModel.currentDayList.collectAsStateWithLifecycle().value

    val nextDayForecast =
        favoriteViewModel.nextDayList.collectAsStateWithLifecycle().value

    val thirdDayForecast =
        favoriteViewModel.thirdDayList.collectAsStateWithLifecycle().value

    val fourthDayForecast =
        favoriteViewModel.fourthDayList.collectAsStateWithLifecycle().value

    val fifthDayForecast =
        favoriteViewModel.fifthDayList.collectAsStateWithLifecycle().value

    val sixthDayForecast =
        favoriteViewModel.sixthDayList.collectAsStateWithLifecycle().value

    val listOfDays = listOf(
        currentDayForecast,
        nextDayForecast,
        thirdDayForecast,
        fourthDayForecast,
        fifthDayForecast,
        sixthDayForecast
    )

    when (selectedWeather) {
        is Response.Failure -> FailureDisplay(selectedWeather.exception)
        Response.Loading -> LoadingDisplay()
        is Response.Success<*> -> {
            selectedWeather as Response.Success<CurrentWeatherResponse>
            bottomNavigationBarViewModel.setCurrentWeatherTheme(
                selectedWeather.result?.weather?.firstOrNull()?.icon ?: ""
            )
            when (countryName) {
                is Response.Failure -> Text(countryName.exception)
                Response.Loading -> CircularProgressIndicator(color = Color.Black)
                is Response.Success<*> -> {
                    countryName as Response.Success<Address>
                    when (selectedFiveDaysWeatherForecast) {
                        is Response.Failure -> {}
                        Response.Loading -> {}
                        is Response.Success<*> -> {
                            selectedFiveDaysWeatherForecast as Response.Success<List<WeatherItem>>
                            selectedWeather.result?.let {
                                it.latitude = latitude
                                it.longitude = longitude
                                it.countryName = countryName.result?.countryName ?: ""
                                it.cityName = countryName.result?.locality ?: ""

                                selectedFiveDaysWeatherForecast.result?.let { it1 ->
                                    FiveDaysWeatherForecastResponse(
                                        latitude = latitude,
                                        longitude = longitude,
                                        list = it1
                                    )
                                }
                                    ?.let { it2 ->
                                        favoriteViewModel.updateWeather(
                                            currentWeatherResponse = it,
                                            fiveDaysWeatherForecastResponse = it2
                                        )
                                    }
                            }
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush =
                                getWeatherGradient(
                                    selectedWeather.result?.weather?.firstOrNull()?.icon ?: ""
                                )
                            ),
                    ) {
                        item {
                            WeatherDetails(
                                countryName = countryName,
                                selectedWeather = selectedWeather.result,
                                fiveDaysWeatherForecastUiState = selectedFiveDaysWeatherForecast,
                                listOfDays = listOfDays
                            )
                        }
                    }
                }
            }
        }
    }
}

