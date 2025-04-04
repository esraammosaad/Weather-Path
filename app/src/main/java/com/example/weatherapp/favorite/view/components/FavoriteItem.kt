package com.example.weatherapp.favorite.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.favorite.view.screens.deleteFavoriteItem
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.example.weatherapp.landing.view.AnimatedBackground
import com.example.weatherapp.utilis.Styles
import com.example.weatherapp.utilis.getWeatherBackground
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.ConfirmationDialog
import com.example.weatherapp.utilis.view.WeatherStatusImageDisplay
import kotlinx.coroutines.CoroutineScope

@Composable
fun FavoriteItem(
    selectedWeather: CurrentWeatherResponse?,
    cName: String, ciName: String = "",
    snackBarHostState: SnackbarHostState,
    favoriteViewModel: FavoriteViewModelImpl,
    alarmsList: Response,
    fiveDaysForecastFavorites: List<FiveDaysWeatherForecastResponse>? = null,
    index: Int = -1,
    coroutineScope: CoroutineScope,
    onFavoriteCardClicked: (longitude: Double, latitude: Double) -> Unit,
) {
    val context = LocalContext.current
    val isDialog = remember { mutableStateOf(false) }
    val showDatePicker = remember { mutableStateOf(false) }
    val datePickerTitle = remember { mutableStateOf("") }
    val dialogTitle = remember { mutableStateOf("") }
    val dialogText = remember { mutableStateOf("") }
    val onConfirmation = remember { mutableStateOf({}) }
    when (alarmsList) {
        is Response.Loading -> {}
        is Response.Failure -> {}
        is Response.Success<*> -> {
            alarmsList as Response.Success<List<AlarmModel>>
            val selectedAlarm =
                alarmsList.result?.firstOrNull { it.locationId == selectedWeather?.id }
            if (isDialog.value) {
                ConfirmationDialog(
                    onConfirmation = {
                        onConfirmation.value.invoke()
                        isDialog.value = false
                    },
                    confirmText = R.string.confirm,
                    onDismiss = { isDialog.value = false },
                    dialogTitle = dialogTitle.value,
                    dialogText = dialogText.value,
                )
            }
            if (showDatePicker.value) {
                DateAndTimePickerForSet(
                    showDatePicker = showDatePicker,
                    title = datePickerTitle,
                    selectedWeather = selectedWeather,
                    favoriteViewModel = favoriteViewModel,
                )
            }
            Box {
                Box(modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clickable {
                        onFavoriteCardClicked.invoke(
                            selectedWeather?.longitude ?: 0.0,
                            selectedWeather?.latitude ?: 0.0
                        )
                    }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .background(
                                brush = getWeatherGradient(
                                    selectedWeather?.weather?.firstOrNull()?.icon ?: ""
                                ),
                                shape = RoundedCornerShape(25.dp)
                            ),
                        elevation = CardDefaults.cardElevation(),
                        colors = CardColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.White
                        )
                    ) {
                        Box {
                            Column {
                                AnimatedBackground(
                                    getWeatherBackground(
                                        selectedWeather?.weather?.firstOrNull()?.icon ?: ""
                                    )
                                )
                                AnimatedBackground(
                                    getWeatherBackground(
                                        selectedWeather?.weather?.firstOrNull()?.icon ?: ""
                                    )
                                )
                            }
                            Column(
                                verticalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .height(210.dp)
                                    .padding(24.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text(
                                            text = ciName.ifEmpty { stringResource(R.string.current_location) },
                                            style = Styles.textStyleSemiBold20,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.fillMaxWidth(0.6f)
                                        )
                                        Text(
                                            text = cName,
                                            style = Styles.textStyleNormal16
                                        )
                                    }
                                    Row {
                                        Text(
                                            text = selectedWeather?.main?.temp.toString(),
                                            style = Styles.textStyleMedium30
                                        )

                                        Text(
                                            text = stringResource(
                                                LocalStorageDataSource.getInstance(context).getTempSymbol
                                            ),
                                            style = Styles.textStyleMedium28
                                        )
                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        Text(
                                            text = selectedWeather?.weather?.firstOrNull()?.description
                                                ?: "",
                                            style = Styles.textStyleMedium18
                                        )
                                        WeatherStatusImageDisplay(
                                            selectedWeather?.weather?.firstOrNull()?.icon ?: ""
                                        )
                                    }
                                    Text(
                                        text = stringResource(R.string.h) + " " + "${selectedWeather?.main?.temp_max?.toInt()}" +
                                                stringResource(
                                                    LocalStorageDataSource.getInstance(
                                                        context
                                                    ).getTempSymbol
                                                ) + " " + stringResource(R.string.l) + " " +
                                                "${selectedWeather?.main?.temp_min?.toInt()}" +
                                                stringResource(
                                                    LocalStorageDataSource.getInstance(
                                                        context
                                                    ).getTempSymbol
                                                ),
                                        style = Styles.textStyleMedium16
                                    )

                                }
                                Spacer(modifier = Modifier.height(18.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CustomDateAndTimeOfAlarm(selectedAlarm)
                                    CustomSwitch(
                                        selectedAlarm,
                                        context,
                                        isDialog,
                                        dialogTitle,
                                        dialogText,
                                        onConfirmation,
                                        showDatePicker,
                                        datePickerTitle,
                                        selectedWeather,
                                        favoriteViewModel,
                                        coroutineScope,
                                        snackBarHostState
                                    )
                                }
                            }
                        }
                    }
                }
                if (index != -1) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_remove_24),
                        contentDescription = stringResource(R.string.delete_icon),
                        tint = Color.White,
                        modifier = Modifier
                            .align(alignment = Alignment.TopEnd)
                            .padding(top = 5.dp, end = 10.dp)
                            .size(26.dp)
                            .clickable {
                                deleteFavoriteItem(
                                    selectedWeather,
                                    fiveDaysForecastFavorites,
                                    index,
                                    favoriteViewModel,
                                    coroutineScope,
                                    snackBarHostState,
                                    context,
                                    selectedAlarm
                                )
                            }
                    )
                }
            }

        }
    }
}