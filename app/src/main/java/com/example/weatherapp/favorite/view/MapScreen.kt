package com.example.weatherapp.favorite.view

import android.location.Geocoder
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.home.view_model.HomeViewModelImpl
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.getWeatherGradient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen(viewModel: HomeViewModelImpl) {
    val context = LocalContext.current
    val markerState = rememberMarkerState(position = LatLng(40.9971, 29.1007))
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
  Box{
      TextField(value = textFieldValue.value, onValueChange = {
          textFieldValue.value=it
      })
      GoogleMap(
          modifier = Modifier.fillMaxSize(),
          cameraPositionState = cameraPositionState,
          properties = properties,
          uiSettings = uiSettings,
          onMapClick = {
              markerState.position = it
              viewModel.getCurrentWeather(longitude = it.longitude, latitude = it.latitude)
              viewModel.getFiveDaysWeatherForecast(longitude = it.longitude, latitude = it.latitude)
              viewModel.getCountryName(longitude = it.longitude, latitude = it.latitude,geocoder)
              markerState.showInfoWindow()
          },

          ) {
          val currentWeatherResponse = viewModel.currentWeather.observeAsState().value
          val countryName = viewModel.countryName.observeAsState().value



          MarkerInfoWindow(
              state = markerState,
              draggable = true,

              ) {
              Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center,
                  modifier = Modifier
                      .border(
                          BorderStroke(
                              1.dp,
                              getWeatherGradient(currentWeatherResponse?.weather?.get(0)?.icon ?: "")
                          ),
                          RoundedCornerShape(50)
                      )
                      .clip(RoundedCornerShape(10))
                      .background(
                          getWeatherGradient(
                              currentWeatherResponse?.weather?.get(0)?.icon ?: ""
                          )
                      )
                      .padding(20.dp)
              ) {


                  Text(
                      "${countryName?.locality}",
                      fontWeight = FontWeight.Bold,
                      color = Color.White,
                      fontFamily = poppinsFontFamily,
                      fontSize = 16.sp
                  )
                  Spacer(modifier = Modifier.height(5.dp))
                  Text(
                      "${countryName?.countryName}",
                      fontWeight = FontWeight.Bold,
                      color = Color.White,
                      fontFamily = poppinsFontFamily,
                      fontSize = 16.sp
                  )
                  Spacer(modifier = Modifier.height(5.dp))
                  Text(
                      currentWeatherResponse?.main?.temp.toString() + Strings.CELSIUS_SYMBOL,
                      fontWeight = FontWeight.Bold,
                      color = Color.White,
                      fontFamily = poppinsFontFamily,
                      fontSize = 16.sp
                  )
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


  }



}





