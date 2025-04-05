package com.example.weatherapp.utilis.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.getWeatherBackground
import com.example.weatherapp.utilis.getWeatherGradient

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartialBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    selectedWeather: CurrentWeatherResponse?,
    countryNameUiState: Response,
    fiveDaysWeatherForecastResponse: Response,
    listOfDays: List<List<WeatherItem>>,
    text:Int,
    onAddClicked: () -> Unit,
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
                                selectedWeather?.weather?.firstOrNull()?.icon ?: ""
                            )
                        ),
                ) {
                    item {

                        Box {
                            AnimatedPreloader(
                                getWeatherBackground(
                                    selectedWeather?.weather?.firstOrNull()?.icon ?: ""
                                )
                            )
                            Column {

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
                                        stringResource(text),
                                        fontFamily = poppinsFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp,
                                        color = Color.White,
                                        modifier = Modifier.clickable {
                                            onAddClicked.invoke()
                                        }
                                    )
                                }
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
    }
}