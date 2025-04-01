package com.example.weatherapp.home.view

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.favorite.view.components.BottomSheetDisplay
import com.example.weatherapp.favorite.view.components.CustomMarkerWindow
import com.example.weatherapp.favorite.view.components.SearchableMapScreen
import com.example.weatherapp.home.view_model.HomeViewModelImpl
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.util.Locale


@Composable
fun LocationPickScreen(
    onBackClicked: () -> Unit,
    homeViewModel: HomeViewModelImpl,
    location: Location,
    isConnected: Boolean,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel,
    onChooseClicked: () -> Unit,
) {
    val context = LocalContext.current

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

    val geocoder =
        Geocoder(context, Locale(LocalStorageDataSource.getInstance(context).getLanguageCode))

    val currentWeatherUiState =
        homeViewModel.currentWeather.collectAsStateWithLifecycle().value

    val currentDayForecast =
        homeViewModel.currentDayList.collectAsStateWithLifecycle().value

    val nextDayForecast =
        homeViewModel.nextDayList.collectAsStateWithLifecycle().value

    val thirdDayForecast =
        homeViewModel.thirdDayList.collectAsStateWithLifecycle().value

    val fourthDayForecast =
        homeViewModel.fourthDayList.collectAsStateWithLifecycle().value

    val fifthDayForecast =
        homeViewModel.fifthDayList.collectAsStateWithLifecycle().value

    val sixthDayForecast =
        homeViewModel.sixthDayList.collectAsStateWithLifecycle().value

    val fiveDaysWeatherForecast =
        homeViewModel.fiveDaysWeatherForecast.collectAsStateWithLifecycle().value

    val countryName = homeViewModel.countryName.collectAsStateWithLifecycle().value

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

    GetLocation(markerState, homeViewModel, geocoder, isConnected)

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
            countryName
        )
    }
    SearchableMapScreen(cameraPositionState, markerState, showBottomSheet)
    BottomSheetDisplay(
        currentWeatherUiState,
        markerState,
        fiveDaysWeatherForecast,
        countryName,
        showBottomSheet,
        listOfDays,
    ) { selectedWeather, selectedFiveDaysForecast ->
        onChooseClicked.invoke()
        showBottomSheet.value = false
        LocalStorageDataSource.getInstance(context).savePickedLong(selectedWeather.longitude)
        LocalStorageDataSource.getInstance(context).savePickedLat(selectedWeather.latitude)
//        Log.i(
//            "TAG",
//            "LocationPickScreen: ${LocalStorageDataSource.getInstance(context).getPickedLat}"
//        )
//        Log.i(
//            "TAG",
//            "LocationPickScreen: ${LocalStorageDataSource.getInstance(context).getPickedLong}"
//        )
        location.latitude = LocalStorageDataSource.getInstance(context).getPickedLat
        location.longitude = LocalStorageDataSource.getInstance(context).getPickedLong
//
//        when(countryName){
//            is Response.Failure -> TODO()
//            Response.Loading -> TODO()
//            is Response.Success<*> -> {
//                countryName as Response.Success<Address>
//                homeViewModel.insertCurrentWeather(
//                    selectedWeather,
//                    countryName.result,
//                    location
//                )
//
//
//            }
//        }
//
//        homeViewModel.insertFiveDaysForecast(
//            selectedFiveDaysForecast.list,
//            location
//        )
    }
}

@Composable
private fun GetLocation(
    markerState: MarkerState,
    homeViewModel: HomeViewModelImpl,
    geocoder: Geocoder,
    isConnected: Boolean
) {
    val context = LocalContext.current
    val languageCode = LocalStorageDataSource.getInstance(context).getLanguageCode
    val tempUnit = LocalStorageDataSource.getInstance(context).getTempUnit
    LaunchedEffect(markerState.position) {
        homeViewModel.getCurrentWeather(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude,
            isConnected = isConnected,
            languageCode = languageCode,
            tempUnit = tempUnit
        )
        homeViewModel.getFiveDaysWeatherForecast(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude,
            isConnected = isConnected,
            languageCode = languageCode,
            tempUnit = tempUnit
        )
        homeViewModel.getCountryName(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude,
            geocoder = geocoder,
            isConnected = isConnected
        )
    }
}
