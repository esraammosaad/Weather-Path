package com.example.weatherapp.favorite_alarm_features.favorite.view

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
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
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteAndAlarmSharedViewModelImpl
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.FailureDisplay
import com.example.weatherapp.utilis.view.LoadingDisplay
import com.example.weatherapp.utilis.view.WeatherDetails
import java.math.RoundingMode
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherDetailsScreen(
    longitude: Double,
    latitude: Double,
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel,
    isConnected: Boolean,
    snackBarHostState: SnackbarHostState
) {

    Log.i("TAG", "WeatherDetailsScreen: $longitude $longitude")

    val long = longitude.toBigDecimal()
        .setScale(2, RoundingMode.DOWN).toDouble()

    val lat = latitude.toBigDecimal()
        .setScale(2, RoundingMode.DOWN).toDouble()
    Log.i("TAG", "WeatherDetailsScreen: $long $lat")


    val context = LocalContext.current
//    LaunchedEffect(Unit) {
//        favoriteViewModel.message.collect {
//            snackBarHostState.showSnackbar(context.getString(it))
//        }
//    }
    val languageCode = LocalStorageDataSource.getInstance(context).getLanguageCode
    val tempUnit = LocalStorageDataSource.getInstance(context).getTempUnit

    LaunchedEffect(Unit) {
        favoriteViewModel.getSelectedWeather(
            longitude = long,
            latitude = lat,
            languageCode = languageCode,
            tempUnit = tempUnit,
            isConnected = isConnected
        )
        favoriteViewModel.getSelectedFiveDaysWeatherForecast(
            longitude = long,
            latitude = lat,
            languageCode = languageCode,
            tempUnit = tempUnit,
            isConnected = isConnected
        )
        val langCode = LocalStorageDataSource.getInstance(context).getLanguageCode
        val geocoder =
            Geocoder(
                context,
                Locale(if (langCode.length > 2) langCode.substring(0, 2) else langCode)
            )
        favoriteViewModel.getCountryName(
            longitude = long, latitude = lat, geocoder, isConnected = isConnected
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
                                it.latitude = lat
                                it.longitude = long
                                it.countryName =
                                    if (it.countryName.isNullOrEmpty()) countryName.result?.countryName
                                        ?: it.sys.country else it.countryName

                                it.cityName =
                                    if (it.cityName.isNullOrEmpty()) countryName.result?.locality
                                        ?: it.name else it.cityName

                                selectedFiveDaysWeatherForecast.result?.let { it1 ->
                                    FiveDaysWeatherForecastResponse(
                                        latitude = lat,
                                        longitude = long,
                                        list = it1
                                    )
                                }
                                    ?.let { it2 ->
                                        favoriteViewModel.updateWeather(
                                            currentWeatherResponse = it,
                                            fiveDaysWeatherForecastResponse = it2,
                                            isConnected
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

