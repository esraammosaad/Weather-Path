package com.example.weatherapp.favorite.view

import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.CurrentDateDisplay
import com.example.weatherapp.utilis.view.CustomText
import com.example.weatherapp.utilis.view.FiveDaysWeatherForecastDisplay
import com.example.weatherapp.utilis.view.ImageDisplay
import com.example.weatherapp.utilis.view.LocationDisplay
import com.example.weatherapp.utilis.view.MoreDetailsContainer
import com.example.weatherapp.utilis.view.TemperatureDisplay
import com.example.weatherapp.utilis.view.WeatherForecastDisplay
import com.example.weatherapp.utilis.view.WeatherStatusDisplay
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.joinAll

@Composable
fun MapScreen(favoriteViewModel: FavoriteViewModelImpl, location: Location) {
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

    val geocoder = Geocoder(context)
    val textFieldValue = remember { mutableStateOf("") }

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
    val message = favoriteViewModel.message.collectAsStateWithLifecycle().value
    val showBottomSheet = remember { mutableStateOf(false) }

    val selectedWeather: CurrentWeatherResponse?
    val selectedFiveDaysForecast: FiveDaysWeatherForecastResponse?
    val selectedCountryName: Address?

    val listOfDays = listOf(
        currentDayForecast,
        nextDayForecast,
        thirdDayForecast,
        fourthDayForecast,
        fifthDayForecast,
        sixthDayForecast
    )


    LaunchedEffect(Unit) {
        favoriteViewModel.getSelectedWeather(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude
        )
        favoriteViewModel.getSelectedFiveDaysWeatherForecast(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude
        )
        favoriteViewModel.getCountryName(
            longitude = markerState.position.longitude,
            latitude = markerState.position.latitude,
            geocoder
        )
    }



    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = properties,
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings,
        onMapClick = {
            markerState.position = it
            favoriteViewModel.getSelectedWeather(
                longitude = markerState.position.longitude,
                latitude = markerState.position.latitude
            )
            favoriteViewModel.getSelectedFiveDaysWeatherForecast(
                longitude = markerState.position.longitude,
                latitude = markerState.position.latitude
            )
            favoriteViewModel.getCountryName(
                longitude = markerState.position.longitude,
                latitude = markerState.position.latitude,
                geocoder
            )
            showBottomSheet.value = true
        },
        onMapLoaded = {
            markerState.showInfoWindow()
        }
    ) {

        MarkerInfoWindow(
            state = markerState,
            draggable = true,
            onInfoWindowClick = {
                it.hideInfoWindow()
            }

            ) {
            when (currentWeatherUiState) {
                Response.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(25.dp),
                    color = Color.Black
                )

                is Response.Failure -> Text(currentWeatherUiState.exception)
                is Response.Success<*> -> {

                    currentWeatherUiState as Response.Success<CurrentWeatherResponse>
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .border(
                                BorderStroke(
                                    1.dp,
                                    getWeatherGradient(
                                        currentWeatherUiState.result?.weather?.get(0)?.icon ?: ""
                                    )
                                ),
                                RoundedCornerShape(50)
                            )
                            .clip(RoundedCornerShape(10))
                            .background(
                                getWeatherGradient(
                                    currentWeatherUiState.result?.weather?.get(0)?.icon ?: ""
                                )
                            )
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
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = poppinsFontFamily,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    countryName.result?.countryName
                                        ?: currentWeatherUiState.result?.sys?.country ?: "",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = poppinsFontFamily,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            currentWeatherUiState.result?.main?.temp.toString() + Strings.CELSIUS_SYMBOL,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = poppinsFontFamily,
                            fontSize = 16.sp
                        )
                    }
                }
            }

        }

    }

    when (currentWeatherUiState) {

        is Response.Loading -> {
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) { CircularProgressIndicator(color = Color.Black) }
        }

        is Response.Failure -> {
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) { Text(currentWeatherUiState.exception) }
        }

        is Response.Success<*> -> {
            markerState.showInfoWindow()
            currentWeatherUiState as Response.Success<CurrentWeatherResponse>
            selectedWeather = currentWeatherUiState.result
            selectedWeather?.latitude = markerState.position.latitude
            selectedWeather?.longitude = markerState.position.longitude
            when (fiveDaysWeatherForecast) {
                is Response.Loading -> CircularProgressIndicator()
                is Response.Failure -> Text(fiveDaysWeatherForecast.exception)
                is Response.Success<*> -> {
                    fiveDaysWeatherForecast as Response.Success<List<WeatherItem>>
                    selectedFiveDaysForecast = fiveDaysWeatherForecast.result?.let {
                        FiveDaysWeatherForecastResponse(
                            it,
                            longitude = markerState.position.longitude,
                            latitude = markerState.position.latitude
                        )
                    }
                    when (countryName) {
                        is Response.Loading -> CircularProgressIndicator()
                        is Response.Failure -> Text(countryName.exception)
                        is Response.Success<*> -> {
                            countryName as Response.Success<Address>
                            selectedCountryName = countryName.result
                            selectedWeather?.countryName = countryName.result?.countryName ?: ""
                            selectedWeather?.cityName = countryName.result?.locality ?: ""
                            currentWeatherUiState.result?.countryName =
                                countryName.result?.countryName ?: ""
                            currentWeatherUiState.result?.cityName =
                                countryName.result?.locality ?: ""
                            PartialBottomSheet(
                                showBottomSheet,
                                currentWeatherUiState.result,
                                countryName.result,
                                fiveDaysWeatherForecast,
                                listOfDays,
                            ) {
                                if (selectedWeather != null && selectedFiveDaysForecast != null)
                                    favoriteViewModel.insertWeather(
                                        selectedWeather,
                                        selectedFiveDaysForecast
                                    )
                                showBottomSheet.value = false
                            }


                        }
                    }
                }
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartialBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    selectedWeatherUiState: CurrentWeatherResponse?,
    countryNameUiState: Address?,
    fiveDaysWeatherForecastUiState: Response,
    listOfDays: List<List<WeatherItem>>,
    onAddClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
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
                                selectedWeatherUiState?.weather?.get(0)?.icon ?: ""
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
                                    onAddClick.invoke()
                                }

                            )


                        }

                        ImageDisplay()

                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center

                        ) {

                            CustomText(text = "TODAY")
                            Spacer(modifier = Modifier.height(3.dp))

                            countryNameUiState?.let {
                                selectedWeatherUiState?.let { it1 ->
                                    LocationDisplay(
                                        it,
                                        it1
                                    )
                                }
                            }


                            Spacer(modifier = Modifier.height(5.dp))
                            CurrentDateDisplay()
                            Spacer(modifier = Modifier.height(5.dp))
                            selectedWeatherUiState?.let { TemperatureDisplay(it) }
                            selectedWeatherUiState?.let { WeatherStatusDisplay(it) }
                            WeatherForecastDisplay(
                                fiveDaysWeatherForecastUiState,
                                selectedWeatherUiState?.weather?.get(0)?.icon
                                    ?: ""
                            )
                            selectedWeatherUiState?.let {
                                MoreDetailsContainer(
                                    it
                                )
                            }

                            FiveDaysWeatherForecastDisplay(
                                fiveDaysWeatherForecast = listOfDays
                            )
                        }
                    }
                }


            }

        }


    }
}


//        Marker(
//            state = markerState,
//            title = "One Marker",
//            snippet = "come",
//            draggable = true,
//        )


//        MarkerComposable(
//            state = MarkerState(position = locationMap),
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.cloud_sketched),
//                contentDescription = "",
//            )
//        }















