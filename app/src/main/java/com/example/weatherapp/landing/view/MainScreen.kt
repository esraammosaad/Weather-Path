package com.example.weatherapp.landing.view

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Location
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.home_settings_feature.view_model.HomeAndSettingsSharedViewModelImpl
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.view.FailureDisplay
import com.example.weatherapp.utilis.view.LoadingDisplay
import com.example.weatherapp.utilis.view.NavHostImpl

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    currentWeather: Response,
    isSeenGetStartedScreen: MutableState<Boolean>,
    countryName: Response,
    fiveDaysWeatherForecast: Response,
    navController: NavHostController,
    innerPadding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    locationState: MutableState<Location>,
    isConnected: MutableState<Boolean>,
    homeViewModel: HomeAndSettingsSharedViewModelImpl,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel
) {
    val context = LocalContext.current
    CheckInternet(snackBarHostState, context, isConnected)
    when (currentWeather) {
        is Response.Loading -> LoadingDisplay()
        is Response.Success<*> -> {
            val currentWeatherResponse =
                currentWeather as Response.Success<CurrentWeatherResponse>
            if (LocalStorageDataSource.getInstance(context).getLocationState == R.string.map) {
                currentWeather.result?.longitude =
                    LocalStorageDataSource.getInstance(context).getPickedLong
                currentWeather.result?.latitude =
                    LocalStorageDataSource.getInstance(context).getPickedLat
            }
            bottomNavigationBarViewModel.setCurrentWeatherTheme(
                currentWeatherResponse.result?.weather?.firstOrNull()?.icon
                    ?: ""
            )
            AnimatedVisibility(visible = isSeenGetStartedScreen.value) {
                currentWeatherResponse.result.let {
                    when (countryName) {
                        is Response.Success<*> -> {
                            val cName = countryName as Response.Success<Address>
                            when (fiveDaysWeatherForecast) {
                                is Response.Failure -> {}
                                Response.Loading -> {}
                                is Response.Success<*> -> {
                                    val fiveDaysWeatherForecastResponse =
                                        fiveDaysWeatherForecast as Response.Success<List<WeatherItem>>

                                    if (isConnected.value) {
                                        insertCurrentLocation(
                                            it,
                                            cName,
                                            fiveDaysWeatherForecastResponse,
                                            locationState,
                                            context,
                                            homeViewModel
                                        )
                                    }
                                }
                            }
                            if (it != null) {
                                NavHostImpl(
                                    navController = navController,
                                    currentWeatherResponse = it,
                                    countryName = cName.result,
                                    innerPadding = innerPadding,
                                    snackBarHostState = snackBarHostState,
                                    homeViewModel = homeViewModel,
                                    bottomNavigationBarViewModel = bottomNavigationBarViewModel,
                                    locationState = locationState,
                                    isConnected = isConnected.value,

                                    )
                            }
                        }

                        is Response.Failure -> FailureDisplay(
                            if (isConnected.value)
                                countryName.exception
                            else stringResource(R.string.no_internet_connection)
                        )

                        Response.Loading -> LoadingDisplay()
                    }
                }


            }
        }

        is Response.Failure -> FailureDisplay(currentWeather.exception)
    }
}

private fun insertCurrentLocation(
    it: CurrentWeatherResponse?,
    cName: Response.Success<Address>,
    fiveDaysWeatherForecastResponse: Response.Success<List<WeatherItem>>,
    locationState: MutableState<Location>,
    context: Context,
    homeViewModel: HomeAndSettingsSharedViewModelImpl
) {
    if (LocalStorageDataSource.getInstance(context).getLocationState == R.string.gps) {
        if (locationState.value.latitude != 0.0 && locationState.value.longitude != 0.0) {
            homeViewModel.insertCurrentWeather(
                currentWeather = it,
                latitude = locationState.value.latitude,
                longitude = locationState.value.longitude,
                countryName = cName.result,
            )
            homeViewModel.insertFiveDaysForecast(
                fiveDaysWeatherForecast = fiveDaysWeatherForecastResponse.result,
                latitude = locationState.value.latitude,
                longitude = locationState.value.longitude

            )
        }
    }
}

@Composable
private fun CheckInternet(
    snackBarHostState: SnackbarHostState,
    context: Context,
    isConnected: MutableState<Boolean>
) {
    LaunchedEffect(isConnected.value) {
        if (!isConnected.value) {
            val res = snackBarHostState.showSnackbar(
                context.getString(R.string.no_internet_connection),
                actionLabel = context.getString(R.string.network_settings),
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
            )
            when (res) {
                SnackbarResult.ActionPerformed -> {
                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));
                }

                SnackbarResult.Dismissed -> {
                }
            }

        }
    }
}