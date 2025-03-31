package com.example.weatherapp.favorite.view.screens

import android.app.Activity
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.Styles
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.WeatherDetails
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.util.Locale

@Composable
fun MapScreen(
    favoriteViewModel: FavoriteViewModelImpl,
    location: Location,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel,
    snackBarHostState: SnackbarHostState
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

    val geocoder = Geocoder(context, Locale(LocalStorageDataSource.getInstance(context).getLanguageCode))

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

    val selectedWeather: CurrentWeatherResponse?
    val selectedFiveDaysForecast: FiveDaysWeatherForecastResponse?

    val listOfDays = listOf(
        currentDayForecast,
        nextDayForecast,
        thirdDayForecast,
        fourthDayForecast,
        fifthDayForecast,
        sixthDayForecast
    )

    GetLocation(markerState, favoriteViewModel, geocoder)

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

    when (currentWeatherUiState) {

        is Response.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) { CircularProgressIndicator(color = Color.Black) }
        }

        is Response.Failure -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) { Text(currentWeatherUiState.exception) }
        }

        is Response.Success<*> -> {
            markerState.showInfoWindow()
            currentWeatherUiState as Response.Success<CurrentWeatherResponse>
            selectedWeather = currentWeatherUiState.result
            selectedWeather?.latitude =
                markerState.position.latitude.toBigDecimal().setScale(2, RoundingMode.DOWN)
                    .toDouble()
            selectedWeather?.longitude =
                markerState.position.longitude.toBigDecimal().setScale(2, RoundingMode.DOWN)
                    .toDouble()
            when (fiveDaysWeatherForecast) {
                is Response.Loading -> CircularProgressIndicator()
                is Response.Failure -> Text(fiveDaysWeatherForecast.exception)
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
                        is Response.Loading -> CircularProgressIndicator()
                        is Response.Failure -> Text(countryName.exception)
                        is Response.Success<*> -> {
                            countryName as Response.Success<Address>
                            selectedWeather?.countryName = countryName.result?.countryName ?: ""
                            selectedWeather?.cityName = countryName.result?.locality ?: ""
                            PartialBottomSheet(
                                showBottomSheet,
                                selectedWeather,
                                countryName.result,
                                fiveDaysWeatherForecast,
                                listOfDays,
                                favoriteViewModel,
                                snackBarHostState,
                                selectedFiveDaysForecast
                            )


                        }
                    }
                }
            }
        }
    }


}

@Composable
private fun CustomMarkerWindow(
    markerState: MarkerState,
    currentWeatherUiState: Response,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel,
    countryName: Response
) {
    MarkerInfoWindow(
        state = markerState,
        draggable = true,
        onInfoWindowClick = { it.hideInfoWindow() }
    ) {
        when (currentWeatherUiState) {
            Response.Loading -> CircularProgressIndicator(
                modifier = Modifier.size(25.dp),
                color = Color.Black
            )

            is Response.Failure -> Text(currentWeatherUiState.exception)
            is Response.Success<*> -> {
                currentWeatherUiState as Response.Success<CurrentWeatherResponse>
                bottomNavigationBarViewModel.setCurrentWeatherTheme(
                    currentWeatherUiState.result?.weather?.firstOrNull()?.icon ?: ""
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .border(
                            BorderStroke(1.dp, weatherCustomColor(currentWeatherUiState)),
                            RoundedCornerShape(50)
                        )
                        .clip(RoundedCornerShape(10))
                        .background(weatherCustomColor(currentWeatherUiState))
                        .padding(20.dp)
                ) {
                    when (countryName) {
                        is Response.Failure -> {}
                        Response.Loading -> {}
                        is Response.Success<*> -> {
                            countryName as Response.Success<Address>
                            Text(
                                countryName.result?.locality
                                    ?: currentWeatherUiState.result?.name ?: "",
                                style = Styles.textStyleBold16,
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                countryName.result?.countryName
                                    ?: currentWeatherUiState.result?.sys?.country ?: "",
                                style = Styles.textStyleBold16,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        currentWeatherUiState.result?.main?.temp.toString() + Strings.CELSIUS_SYMBOL,
                        style = Styles.textStyleBold16,
                    )
                }
            }
        }

    }
}

@Composable
private fun weatherCustomColor(currentWeatherUiState: Response.Success<CurrentWeatherResponse>) =
    getWeatherGradient(currentWeatherUiState.result?.weather?.firstOrNull()?.icon ?: "")

@Composable
private fun GetLocation(
    markerState: MarkerState,
    favoriteViewModel: FavoriteViewModelImpl,
    geocoder: Geocoder
) {
    val context = LocalContext.current
    val languageCode = LocalStorageDataSource.getInstance(context).getLanguageCode
    val tempUnit = LocalStorageDataSource.getInstance(context).getTempUnit
    LaunchedEffect(markerState.position) {
        favoriteViewModel.getSelectedWeather(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude,
            isConnected = true,
            languageCode = languageCode,
            tempUnit = tempUnit
        )
        favoriteViewModel.getSelectedFiveDaysWeatherForecast(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude,
            isConnected = true,
            languageCode = languageCode,
            tempUnit = tempUnit
        )
        favoriteViewModel.getCountryName(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude,
            geocoder = geocoder,
            isConnected = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartialBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    selectedWeather: CurrentWeatherResponse?,
    countryNameUiState: Address?,
    fiveDaysWeatherForecastResponse: Response,
    listOfDays: List<List<WeatherItem>>,
    favoriteViewModel: FavoriteViewModelImpl,
    snackBarHostState: SnackbarHostState,
    selectedFiveDaysForecast: FiveDaysWeatherForecastResponse?,
) {
    val addedItemMessage =
        favoriteViewModel.insertFavoriteItemResult.collectAsStateWithLifecycle().value

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showBottomSheet.value) {
            ModalBottomSheet(
                containerColor = Color.Transparent,
                dragHandle = null,
                modifier = Modifier.fillMaxHeight(),
                sheetState = sheetState,
                scrimColor = Color.Transparent,
                onDismissRequest = { showBottomSheet.value = false }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush =
                            getWeatherGradient(
                                selectedWeather?.weather?.firstOrNull()?.icon ?: ""
                            )
                        ),
                ) {

                    item {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp, end = 18.dp, start = 18.dp)
                        ) {
                            Text(
                                stringResource(R.string.cancel),
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                                color = Color.White,
                                modifier = Modifier.clickable {
                                    showBottomSheet.value = false
                                }

                            )
                            Text(
                                stringResource(R.string.add),
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = Color.White,
                                modifier = Modifier.clickable {

                                    if (selectedWeather != null && selectedFiveDaysForecast != null)
                                        favoriteViewModel.insertWeather(
                                            selectedWeather,
                                            selectedFiveDaysForecast
                                        )
                                    showBottomSheet.value = false

                                    coroutineScope.launch {
                                        val result = snackBarHostState.showSnackbar(
                                            "Location Added Successfully",
                                            actionLabel = "UNDO",
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

                            )


                        }
                    }

                    item {
                        WeatherDetails(
                            countryNameUiState,
                            selectedWeather,
                            fiveDaysWeatherForecastResponse,
                            listOfDays
                        )
                    }
                }


            }

        }


    }
}

@Composable
fun SearchableMapScreen(
    cameraPositionState: CameraPositionState,
    markerState: MarkerState,
    showBottomSheet: MutableState<Boolean>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, Strings.GOOGLE_API_KEY)
        }
    }
    val fields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(context)
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val place = result.data?.let { Autocomplete.getPlaceFromIntent(it) }
                val latLng = place?.latLng
                scope.launch {
                    latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 12f) }
                        ?.let { cameraPositionState.animate(it, 2000) }
                    if (latLng != null) {
                        markerState.position = latLng
                        showBottomSheet.value = true
                    }
                }

            }
        }
    Box(modifier = Modifier.fillMaxWidth()) {
        FloatingActionButton(
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(vertical = 42.dp, horizontal = 12.dp),
            onClick = { launcher.launch(intent) },
            containerColor = PrimaryColor,
            contentColor = Color.White,
            shape = RoundedCornerShape(100.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search)
            )
        }
    }

    //Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
    //                CircularProgressIndicator(modifier = Modifier.size(25.dp), color = Color.Black)
    //            }
}








