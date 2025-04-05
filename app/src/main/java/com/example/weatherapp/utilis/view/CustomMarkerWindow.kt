package com.example.weatherapp.utilis.view

import android.location.Address
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.Styles
import com.example.weatherapp.utilis.convertToArabicNumbers
import com.example.weatherapp.utilis.getWeatherGradient
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState

@Composable
fun CustomMarkerWindow(
    markerState: MarkerState,
    currentWeatherUiState: Response,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel,
    countryName: Response,
    isConnected: Boolean
) {

    val context = LocalContext.current
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

            is Response.Failure -> FailureText(isConnected, currentWeatherUiState)
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
                        is Response.Failure -> {
                            FailureText(isConnected, countryName)
                        }

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
                        convertToArabicNumbers(
                            currentWeatherUiState.result?.main?.temp.toString(),
                            context
                        ) + stringResource(LocalStorageDataSource.getInstance(context).getTempSymbol),
                        style = Styles.textStyleBold16,
                    )
                }
            }
        }
    }
}


@Composable
private fun FailureText(
    isConnected: Boolean,
    currentWeatherUiState: Response.Failure
) {
    Text(
        if (isConnected) currentWeatherUiState.exception else stringResource(R.string.no_internet),
        style = Styles.textStyleBold16,
    )
}

@Composable
private fun weatherCustomColor(currentWeatherUiState: Response.Success<CurrentWeatherResponse>) =
    getWeatherGradient(currentWeatherUiState.result?.weather?.firstOrNull()?.icon ?: "")
