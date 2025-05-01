package com.example.weatherapp.home_settings_feature.settings.view

import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.utilis.view.BottomSheetDisplay
import com.example.weatherapp.utilis.view.CustomMarkerWindow
import com.example.weatherapp.utilis.view.SearchableMapScreen
import com.example.weatherapp.home_settings_feature.view_model.HomeAndSettingsSharedViewModelImpl
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
import java.math.RoundingMode
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LocationPickScreen(
    onBackClicked: () -> Unit,
    homeViewModel: HomeAndSettingsSharedViewModelImpl,
    location: MutableState<Location>,
    isConnected: Boolean,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel,
    onChooseClicked: () -> Unit,
) {
    val context = LocalContext.current

    val markerState =
        rememberMarkerState(position = LatLng(location.value.latitude, location.value.longitude))

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
        Geocoder(context, Locale(if (langCode.length > 2) langCode.substring(0, 2) else langCode))

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
            countryName,
            isConnected
        )
    }
    Image(
        painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
        contentDescription = "back icon",
        modifier = Modifier
            .padding(vertical = 42.dp, horizontal = 16.dp)
            .size(32.dp)
            .clickable { onBackClicked.invoke() }
    )
    SearchableMapScreen(cameraPositionState, markerState, showBottomSheet)
    BottomSheetDisplay(
        currentWeatherUiState,
        markerState,
        fiveDaysWeatherForecast,
        countryName,
        showBottomSheet,
        listOfDays,
        isConnected,
        R.string.choose
    ) { selectedWeather, selectedFiveDaysForecast ->
        LocalStorageDataSource.getInstance(context).saveLocationState(R.string.map)
        LocalStorageDataSource.getInstance(context).savePickedLong(selectedWeather.longitude.toBigDecimal()
            .setScale(2, RoundingMode.DOWN).toDouble())
        LocalStorageDataSource.getInstance(context).savePickedLat(selectedWeather.latitude.toBigDecimal()
            .setScale(2, RoundingMode.DOWN).toDouble())
        showBottomSheet.value = false
        val lat = LocalStorageDataSource.getInstance(context).getPickedLat
        val long = LocalStorageDataSource.getInstance(context).getPickedLong
        if (lat != 0.0 && long != 0.0) {
            homeViewModel.insertCurrentWeather(
                currentWeather = selectedWeather,
                latitude = LocalStorageDataSource.getInstance(context).getPickedLat,
                longitude = LocalStorageDataSource.getInstance(context).getPickedLong,
                countryName = Address(Locale("")),
            )
            homeViewModel.insertFiveDaysForecast(
                fiveDaysWeatherForecast = selectedFiveDaysForecast.list,
                latitude = LocalStorageDataSource.getInstance(context).getPickedLat,
                longitude = LocalStorageDataSource.getInstance(context).getPickedLong
            )
//            location.value.latitude=lat
//            location.value.longitude=long
        }
        onChooseClicked.invoke()
    }
}

@Composable
private fun GetLocation(
    markerState: MarkerState,
    homeViewModel: HomeAndSettingsSharedViewModelImpl,
    geocoder: Geocoder,
    isConnected: Boolean
) {
    val context = LocalContext.current
    val languageCode = LocalStorageDataSource.getInstance(context).getLanguageCode
    val tempUnit = LocalStorageDataSource.getInstance(context).getTempUnit
    LaunchedEffect(markerState.position) {
        val long = markerState.position.longitude.toBigDecimal()
            .setScale(2, RoundingMode.DOWN).toDouble()
        val lat = markerState.position.latitude.toBigDecimal()
            .setScale(2, RoundingMode.DOWN).toDouble()
        LocalStorageDataSource.getInstance(context).savePickedLong(long)
        LocalStorageDataSource.getInstance(context).savePickedLat(lat)
        Log.i("TAG", "GetLocation: $lat $long")
        homeViewModel.getCurrentWeather(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude,
            isConnected = isConnected,
            languageCode =  if (languageCode.length > 2) ConfigurationCompat.getLocales(Resources.getSystem().configuration)
                .get(0).toString().substring(0, 2) else languageCode,
            tempUnit = tempUnit
        )
        homeViewModel.getFiveDaysWeatherForecast(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude,
            isConnected = isConnected,
            languageCode =  if (languageCode.length > 2) ConfigurationCompat.getLocales(Resources.getSystem().configuration)
                .get(0).toString().substring(0, 2) else languageCode,
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
