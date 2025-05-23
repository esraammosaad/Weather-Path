package com.example.weatherapp.favorite_alarm_features.favorite.view


import android.content.Context
import android.content.res.Resources
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.utilis.view.BottomSheetDisplay
import com.example.weatherapp.utilis.view.CustomMarkerWindow
import com.example.weatherapp.utilis.view.SearchableMapScreen
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteAndAlarmSharedViewModelImpl
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.Styles
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreen(
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl,
    location: Location,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel,
    snackBarHostState: SnackbarHostState,
    isConnected: Boolean
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        favoriteViewModel.message.collect {
            snackBarHostState.showSnackbar(context.getString(it))
        }
    }

    val markerState = rememberMarkerState(position = LatLng(location.latitude, location.longitude))

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerState.position, 15f)
    }

    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
    }

    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }
    val langCode = LocalStorageDataSource.getInstance(context).getLanguageCode

    val geocoder =
        Geocoder(context, Locale( if (langCode.length > 2) ConfigurationCompat.getLocales(Resources.getSystem().configuration)
            .get(0).toString().substring(0, 2) else langCode))

    val currentWeatherUiState =
        favoriteViewModel.selectedWeather.collectAsStateWithLifecycle().value

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

    val fiveDaysWeatherForecast =
        favoriteViewModel.selectedFiveDaysWeatherForecast.collectAsStateWithLifecycle().value

    val countryName = favoriteViewModel.countryName.collectAsStateWithLifecycle().value

    val showBottomSheet = remember { mutableStateOf(false) }

    val listOfDays = listOf(
        currentDayForecast,
        nextDayForecast,
        thirdDayForecast,
        fourthDayForecast,
        fifthDayForecast,
        sixthDayForecast
    )
    val coroutineScope = rememberCoroutineScope()
    GetLocation(markerState, favoriteViewModel, geocoder, isConnected)
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = properties,
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings,
        onMapClick = {
            markerState.position = it
            showBottomSheet.value = true
        },
        onMapLoaded = {
            markerState.showInfoWindow()
        },
    ) {
        CustomMarkerWindow(
            markerState,
            currentWeatherUiState,
            bottomNavigationBarViewModel,
            countryName,
            isConnected
        )
    }
    NoInternetConnectionText(isConnected)
    SearchableMapScreen(cameraPositionState, markerState, showBottomSheet)
    BottomSheetDisplay(
        currentWeatherUiState,
        markerState,
        fiveDaysWeatherForecast,
        countryName,
        showBottomSheet,
        listOfDays,
        isConnected,
        R.string.add

    ) { selectedWeather, selectedFiveDaysForecast ->
        onAddClicked(
            selectedWeather,
            selectedFiveDaysForecast,
            favoriteViewModel,
            showBottomSheet,
            coroutineScope,
            snackBarHostState,
            context
        )
    }
}

@Composable
private fun NoInternetConnectionText(isConnected: Boolean) {
    if (!isConnected) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.no_internet_connection_check_your_network_settings),
                style = Styles.textStyleSemiBold20,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

    }
}


@Composable
private fun GetLocation(
    markerState: MarkerState,
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl,
    geocoder: Geocoder,
    isConnected: Boolean
) {
    val context = LocalContext.current
    val languageCode = LocalStorageDataSource.getInstance(context).getLanguageCode
    val tempUnit = LocalStorageDataSource.getInstance(context).getTempUnit
    LaunchedEffect(markerState.position) {
        favoriteViewModel.getSelectedWeather(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude,
            isConnected = isConnected,
            languageCode =  if (languageCode.length > 2) ConfigurationCompat.getLocales(Resources.getSystem().configuration)
                .get(0).toString().substring(0, 2) else languageCode,
            tempUnit = tempUnit
        )
        favoriteViewModel.getSelectedFiveDaysWeatherForecast(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude,
            isConnected = isConnected,
            languageCode =  if (languageCode.length > 2) ConfigurationCompat.getLocales(Resources.getSystem().configuration)
                .get(0).toString().substring(0, 2) else languageCode,
            tempUnit = tempUnit
        )
        favoriteViewModel.getCountryName(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude,
            geocoder = geocoder,
            isConnected = isConnected
        )
    }
}

fun onAddClicked(
    selectedWeather: CurrentWeatherResponse?,
    selectedFiveDaysForecast: FiveDaysWeatherForecastResponse?,
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl,
    showBottomSheet: MutableState<Boolean>,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    if (selectedWeather != null && selectedFiveDaysForecast != null)
        favoriteViewModel.insertWeather(
            selectedWeather,
            selectedFiveDaysForecast
        )
    showBottomSheet.value = false
    coroutineScope.launch {
        val result = snackBarHostState.showSnackbar(
            context.getString(R.string.location_added_successfully),
            actionLabel = context.getString(R.string.undo),
            withDismissAction = true,
            duration = SnackbarDuration.Long
        )
        when (result) {
            SnackbarResult.ActionPerformed -> {
                if (selectedWeather != null && selectedFiveDaysForecast != null) {
                    favoriteViewModel.deleteWeather(
                        currentWeatherResponse = selectedWeather,
                        fiveDaysWeatherForecastResponse = selectedFiveDaysForecast
                    )
                }
            }

            SnackbarResult.Dismissed -> {
            }
        }
    }
}












