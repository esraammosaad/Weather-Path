package com.example.weatherapp.favorite.view.components

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.example.weatherapp.utilis.view.FailureDisplay
import com.example.weatherapp.utilis.view.LoadingDisplay
import kotlinx.coroutines.CoroutineScope

@Composable
fun FavoriteLazyColumn(
    weatherFavorites: Response,
    currentWeatherResponse: CurrentWeatherResponse,
    countryName: String,
    favoriteViewModel: FavoriteViewModelImpl,
    onFavoriteCardClicked: (longitude: Double, latitude: Double) -> Unit,
    snackBarHostState: SnackbarHostState,
    alarms: Response,
    coroutineScope: CoroutineScope,

    ) {
    val selectedWeather = favoriteViewModel.selectedWeather.collectAsStateWithLifecycle().value
    val fiveDaysForecastFavorites =
        favoriteViewModel.fiveDaysForecastFavorites.collectAsStateWithLifecycle().value
    val deleteItemMessage =
        favoriteViewModel.deleteFavoriteItemResult.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    LazyColumn(modifier = Modifier.padding(top = 16.dp, end = 16.dp, start = 16.dp)) {
        item {
            FavoriteItem(
                selectedWeather = currentWeatherResponse,
                cName = countryName,
                snackBarHostState = snackBarHostState,
                favoriteViewModel = favoriteViewModel,
                alarmsList = alarms,
                coroutineScope = coroutineScope,
                onFavoriteCardClicked = onFavoriteCardClicked,
            )
        }
        when (weatherFavorites) {
            is Response.Failure -> item { FailureDisplay(weatherFavorites.exception) }
            Response.Loading -> item { LoadingDisplay() }
            is Response.Success<*> -> {
                weatherFavorites as Response.Success<List<CurrentWeatherResponse>>
                items(weatherFavorites.result?.size ?: 0) { index ->
                    if (currentWeatherResponse.id != weatherFavorites.result?.get(index)?.id) {
                        val item = weatherFavorites.result?.get(index)
                        val coName =
                            item?.countryName?.takeIf { it.isNotEmpty() }
                                ?: item?.sys?.country
                                ?: stringResource(R.string.n_a)
                        val ciName = item?.cityName?.takeIf { it.isNotEmpty() }
                            ?: item?.name
                            ?: stringResource(R.string.n_a)
                        when (fiveDaysForecastFavorites) {
                            is Response.Failure -> {}
                            Response.Loading -> {}
                            is Response.Success<*> -> {
                                fiveDaysForecastFavorites as Response.Success<List<FiveDaysWeatherForecastResponse>>
                                FavoriteItem(
                                    selectedWeather = item,
                                    ciName = ciName,
                                    cName = coName,
                                    snackBarHostState = snackBarHostState,
                                    favoriteViewModel = favoriteViewModel,
                                    alarmsList = alarms,
                                    onFavoriteCardClicked = onFavoriteCardClicked,
                                    index = index,
                                    deleteItemMessage = deleteItemMessage,
                                    fiveDaysForecastFavorites = fiveDaysForecastFavorites.result,
                                    coroutineScope = coroutineScope
                                )

                            }
                        }
                    }
                }


            }
        }
    }
}