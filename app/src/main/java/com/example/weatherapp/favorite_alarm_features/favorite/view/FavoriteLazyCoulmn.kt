package com.example.weatherapp.favorite_alarm_features.favorite.view


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteAndAlarmSharedViewModelImpl
import com.example.weatherapp.utilis.view.FailureDisplay
import kotlinx.coroutines.CoroutineScope

@Composable
fun FavoriteLazyColumn(
    weatherFavorites: Response,
    currentWeatherResponse: CurrentWeatherResponse,
    countryName: String,
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl,
    onFavoriteCardClicked: (longitude: Double, latitude: Double) -> Unit,
    snackBarHostState: SnackbarHostState,
    alarms: Response,
    coroutineScope: CoroutineScope,
) {

    val fiveDaysForecastFavorites = favoriteViewModel.fiveDaysForecastFavorites.collectAsStateWithLifecycle().value
    val favoriteList: MutableState<List<CurrentWeatherResponse>> = remember { mutableStateOf(listOf()) }
    val searchResultList = remember { mutableStateOf(favoriteList.value) }
    val textFieldValue = remember { mutableStateOf("") }


    LaunchedEffect(favoriteList.value.size) {
        searchResultList.value = favoriteList.value
        favoriteViewModel.searchText.collect { ch ->
            searchResultList.value = favoriteList.value.filter {
                it.cityName.contains(ch, ignoreCase = true) || it.countryName.contains(
                    ch,
                    ignoreCase = true
                )
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(top = 16.dp, end = 16.dp, start = 16.dp)
            .fillMaxSize()
    ) {
        item {
            CustomTextField(textFieldValue, favoriteViewModel)
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
            Response.Loading -> item {  }
            is Response.Success<*> -> {
                weatherFavorites as Response.Success<List<CurrentWeatherResponse>>
                favoriteList.value = weatherFavorites.result ?: listOf()
                items(searchResultList.value.size) { index ->
                    if (currentWeatherResponse.id != searchResultList.value[index].id) {
                        val item = searchResultList.value[index]
                        val coName =
                            item.countryName.takeIf { it.isNotEmpty() }
                                ?: item.sys.country
                        val ciName = item.cityName.takeIf { it.isNotEmpty() }
                            ?: item.name
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


//@Composable
//private fun UpdateFavoritesFromApi(
//    isConnected: Boolean,
//    weatherFavorites: Response.Success<List<CurrentWeatherResponse>>,
//    favoriteViewModel: FavoriteViewModelImpl,
//    context: Context
//) {
//    LaunchedEffect(Unit) {
//        Log.i(
//            "TAG",
//            "FavoriteLazyColumn: -----------------------------------------------------------"
//        )
//        if (isConnected) {
//            weatherFavorites.result?.forEach { favorite ->
//                favoriteViewModel.getSelectedWeather(
//                    latitude = favorite.latitude,
//                    longitude = favorite.longitude,
//                    isConnected = true,
//                    languageCode = LocalStorageDataSource.getInstance(context).getLanguageCode,
//                    tempUnit = LocalStorageDataSource.getInstance(context).getTempUnit
//                )
//                favoriteViewModel.getCountryName(
//                    latitude = favorite.latitude,
//                    longitude = favorite.longitude,
//                    isConnected = true,
//                    geocoder = Geocoder(
//                        context,
//                        Locale(LocalStorageDataSource.getInstance(context).getLanguageCode)
//                    )
//                )
//
//                favoriteViewModel.selectedWeather.collect { selectedWeather ->
//                    if (selectedWeather is Response.Success<*>) {
//                        selectedWeather as Response.Success<CurrentWeatherResponse>
//                        favoriteViewModel.countryName.collect { country ->
//                            if (country is Response.Success<*>) {
//                                country as Response.Success<Address>
//                                selectedWeather.result?.let { res ->
//                                    res.cityName = country.result?.locality ?: ""
//                                    res.countryName = country.result?.countryName ?: ""
//                                    res.latitude = favorite.latitude
//                                    res.longitude = favorite.longitude
//                                    favoriteViewModel.updateSelectedWeather(res)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}