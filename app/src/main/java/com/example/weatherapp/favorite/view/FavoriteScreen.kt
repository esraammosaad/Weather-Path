package com.example.weatherapp.favorite.view

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.location.Address
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.R
import com.example.weatherapp.data.managers.WeatherWorkManager
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.Strings.BASE_IMAGE_URL
import com.example.weatherapp.utilis.calculateDelay
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.ConfirmationDialog
import com.example.weatherapp.utilis.view.FailureDisplay
import com.example.weatherapp.utilis.view.LoadingDisplay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.ui.datetimepicker.WheelDateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.now
import java.util.concurrent.TimeUnit


@Composable
fun FavoriteScreen(
    favoriteViewModel: FavoriteViewModelImpl,
    currentWeather: CurrentWeatherResponse,
    countryName: Address,
    onMapClick: () -> Unit,
    onFavoriteCardClicked: (longitude: Double, latitude: Double) -> Unit,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel,
    snackBarHostState: SnackbarHostState,
) {
    favoriteViewModel.selectFavorites()
    val weatherFavorites = favoriteViewModel.weatherFavorites.collectAsStateWithLifecycle().value
    bottomNavigationBarViewModel.setCurrentWeatherTheme(
        currentWeather.weather.firstOrNull()?.icon ?: ""
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = getWeatherGradient(
                    currentWeather.weather.firstOrNull()?.icon ?: ""
                )
            ),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 50.dp, end = 16.dp, start = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.locations), textAlign = TextAlign.Center,
                fontSize = 26.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontFamily = poppinsFontFamily
            )
            Image(
                painter = painterResource(R.drawable.baseline_map_24),
                contentDescription = stringResource(
                    R.string.map_icon,

                    ),
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        onMapClick.invoke()

                    }
            )
        }
        FavoriteLazyColumn(
            weatherFavorites,
            currentWeather,
            countryName.countryName ?: "",
            favoriteViewModel,
            onFavoriteCardClicked,
            snackBarHostState
        )
    }

}

@Composable
fun FavoriteLazyColumn(
    weatherFavorites: Response,
    currentWeatherResponse: CurrentWeatherResponse,
    countryName: String,
    favoriteViewModel: FavoriteViewModelImpl,
    onFavoriteCardClicked: (longitude: Double, latitude: Double) -> Unit,
    snackBarHostState: SnackbarHostState,

    ) {
    val fiveDaysForecastFavorites =
        favoriteViewModel.fiveDaysForecastFavorites.collectAsStateWithLifecycle().value
    val coroutineScope = rememberCoroutineScope()
    val deleteItemMessage =
        favoriteViewModel.deleteFavoriteItemResult.collectAsStateWithLifecycle().value


    LazyColumn(modifier = Modifier.padding(top = 16.dp, end = 16.dp, start = 16.dp)) {
        item {
            FavoriteItem(currentWeatherResponse, countryName, "", onFavoriteCardClicked)

        }
        when (weatherFavorites) {
            is Response.Failure -> item { FailureDisplay(weatherFavorites.exception) }
            Response.Loading -> item { LoadingDisplay() }
            is Response.Success<*> -> {
                weatherFavorites as Response.Success<List<CurrentWeatherResponse>>
                val size: Int = weatherFavorites.result?.size ?: 0
                if (size != 0) {
                    items(size) { index ->
                        if (currentWeatherResponse.id != weatherFavorites.result?.get(index)?.id) {

                            val item = weatherFavorites.result?.get(index)
                            val coName =
                                item?.countryName?.takeIf { it.isNotEmpty() } ?: item?.sys?.country
                                ?: stringResource(
                                    R.string.n_a
                                )
                            val ciName =
                                item?.cityName?.takeIf { it.isNotEmpty() } ?: item?.name
                                ?: stringResource(
                                    R.string.n_a
                                )
                            when (fiveDaysForecastFavorites) {
                                is Response.Failure -> {}
                                Response.Loading -> {}
                                is Response.Success<*> -> {
                                    fiveDaysForecastFavorites as Response.Success<List<FiveDaysWeatherForecastResponse>>
                                    Box {
                                        FavoriteItem(
                                            selectedWeather = item,
                                            cityName = ciName,
                                            countryName = coName,
                                            onFavoriteCardClicked = onFavoriteCardClicked,
                                        )
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
                                                        item,
                                                        fiveDaysForecastFavorites,
                                                        index,
                                                        favoriteViewModel,
                                                        coroutineScope,
                                                        snackBarHostState,
                                                        deleteItemMessage
                                                    )
                                                }
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

private fun deleteFavoriteItem(
    item: CurrentWeatherResponse?,
    fiveDaysForecastFavorites: Response.Success<List<FiveDaysWeatherForecastResponse>>,
    index: Int,
    favoriteViewModel: FavoriteViewModelImpl,
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    deleteItemMessage: String,
) {


    if (item != null) {
        fiveDaysForecastFavorites.result?.getOrNull(
            index
        )
            ?.let {
                favoriteViewModel.deleteWeather(
                    fiveDaysWeatherForecastResponse = it,
                    currentWeatherResponse = item
                )
                scope.launch {
                    val result = snackBarHostState.showSnackbar(
                        message = "Location Deleted Successfully",
                        actionLabel = "UNDO",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long,
                    )
                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            favoriteViewModel.insertWeather(
                                currentWeatherResponse = item,
                                fiveDaysWeatherForecastResponse = it
                            )

                        }

                        SnackbarResult.Dismissed -> {
                        }
                    }

                }
            }
    }
}


@Composable
fun FavoriteItem(
    selectedWeather: CurrentWeatherResponse?,
    countryName: String, cityName: String,
    onFavoriteCardClicked: (longitude: Double, latitude: Double) -> Unit,
) {
    val context = LocalContext.current
    var isDialog by remember { mutableStateOf(false) }
    val showDatePicker = remember { mutableStateOf(false) }
    val datePickerTitle = remember { mutableStateOf("") }
    val selectedTime = remember { mutableStateOf("No alarm set") }
    val selectedDate = remember { mutableStateOf("stay ready!") }
    if (isDialog) {
        ConfirmationDialog(
            onConfirmation = {
                val permissionIntent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:com.example.weatherapp")
                ).addFlags(FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(permissionIntent)
                isDialog = false
            },
            onDismiss = { isDialog = false },
            dialogTitle = "Warning",
            dialogText = "You need to allow draw over other apps permission",
            showRadioButton = false
        ) { }
    }

    DateAndTimePicker(showDatePicker, datePickerTitle, selectedWeather, selectedDate, selectedTime)
    Box(modifier = Modifier
        .padding(bottom = 12.dp)
        .clickable {
            onFavoriteCardClicked.invoke(
                selectedWeather?.longitude ?: 0.0,
                selectedWeather?.latitude ?: 0.0
            )
        }

    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(
                    brush = getWeatherGradient(selectedWeather?.weather?.firstOrNull()?.icon ?: ""),
                    shape = RoundedCornerShape(25.dp)
                )
                .padding(24.dp),
            elevation = CardDefaults.cardElevation(),
            colors = CardColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.White
            )
        ) {

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(210.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = cityName.ifEmpty { stringResource(R.string.current_location) },
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = poppinsFontFamily,
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
                        Text(
                            text = countryName,
                            fontWeight = FontWeight.Normal,
                            fontFamily = poppinsFontFamily,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = selectedWeather?.main?.temp.toString() + Strings.CELSIUS_SYMBOL,
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFontFamily,
                        fontSize = 30.sp
                    )
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
                            text = selectedWeather?.weather?.firstOrNull()?.description ?: "",
                            fontWeight = FontWeight.Medium,
                            fontFamily = poppinsFontFamily,
                            fontSize = 18.sp

                        )
                        Image(
                            painter = rememberAsyncImagePainter(
                                "$BASE_IMAGE_URL${
                                    selectedWeather?.weather?.firstOrNull()?.icon
                                }.png"
                            ),
                            contentDescription = stringResource(R.string.sun_icon),
                            modifier = Modifier
                                .height(30.dp)
                                .width(30.dp)
                                .padding(top = 3.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Text(
                        text = "H:${selectedWeather?.main?.temp_max?.toInt()}${Strings.CELSIUS_SYMBOL}  L:${selectedWeather?.main?.temp_min?.toInt()}${Strings.CELSIUS_SYMBOL}",
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFontFamily,
                        fontSize = 16.sp

                    )

                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var checked by remember { mutableStateOf(false) }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.notification),
                            contentDescription = stringResource(
                                R.string.notification_icon
                            ),
                            Modifier.size(25.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))


                        Column(verticalArrangement = Arrangement.Center) {

                            Text(selectedTime.value, fontSize = 16.sp)
                            Spacer(Modifier.height(5.dp))
                            Text(selectedDate.value, fontSize = 15.sp)
                        }
                    }
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            Log.i("TAG", "FavoriteItem: $it")
                            if (!Settings.canDrawOverlays(context)) {
                                isDialog = true
                            } else {
                                checked = it
                                if (checked) {
                                    showDatePicker.value = true
                                    datePickerTitle.value =
                                        selectedWeather?.cityName ?: ""
                                }
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PrimaryColor,
                            checkedTrackColor = OffWhite,
                            uncheckedThumbColor = PrimaryColor,
                            uncheckedTrackColor = OffWhite,
                        )
                    )
                }
            }

        }
    }
}

private fun requestWorkManager(
    selectedWeather: CurrentWeatherResponse?,
    context: Context,
    duration: Long
) {
    val inputData = Data.Builder()
    inputData.putDouble(Strings.LONG_CONST, selectedWeather?.longitude ?: 0.0)
    inputData.putDouble(Strings.LAT_CONST, selectedWeather?.latitude ?: 0.0)
    inputData.putInt(Strings.CODE_CONST, selectedWeather?.id ?: 0)
    val constraint = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    val workRequest: WorkRequest =
        OneTimeWorkRequestBuilder<WeatherWorkManager>().setConstraints(constraint)
            .setInputData(inputData.build()).setInitialDelay(duration, TimeUnit.MILLISECONDS)
            .build()
    WorkManager.getInstance(context).enqueue(workRequest)
}

@Composable
fun DateAndTimePicker(
    showDatePicker: MutableState<Boolean>,
    title: MutableState<String>,
    selectedWeather: CurrentWeatherResponse?,
    selectedDate: MutableState<String>,
    selectedTime: MutableState<String>
) {
    val context = LocalContext.current

    WheelDateTimePickerView(
        showDatePicker = showDatePicker.value,
        height = 200.dp,
        title = "Set a weather alarm for ${title.value}",
        rowCount = 3,
        minDate = LocalDateTime.now(),
        titleStyle = TextStyle(
            fontSize = 16.sp,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Normal
        ),
        doneLabelStyle = TextStyle(
            fontSize = 14.sp,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Normal,
            color = PrimaryColor,

            ),
        onDoneClick = { snappedDate ->
            selectedTime.value = "${snappedDate.hour}:${snappedDate.minute}"
            selectedDate.value =
                "${snappedDate.dayOfWeek.name} ${snappedDate.dayOfMonth} ${snappedDate.month.name} ${snappedDate.year}"
            val duration = calculateDelay(
                targetYear = snappedDate.year,
                targetMonth = snappedDate.monthNumber,
                targetDay = snappedDate.dayOfMonth,
                targetHour = snappedDate.hour,
                targetMinute = snappedDate.minute,
            )
            requestWorkManager(selectedWeather, context, duration)
            showDatePicker.value = false
        },
        containerColor = OffWhite

    )

}


//    val swipeToDismissState = rememberSwipeToDismissBoxState(
//       confirmValueChange = { state ->
//     if (state == SwipeToDismissBoxValue.EndToStart) {
//    scope.launch {
//     delay(500)
//     deleteFavoriteItem(
//     item,
//    fiveDaysForecastFavorites,
//    index,
//   favoriteViewModel,
//   coroutineScope,
// snackBarHostState,
//  )
//   }
//   true
//   } else {
//    false
//   }
//  }
//   )


// SwipeToDismissBox(
//    state = swipeToDismissState,
//     backgroundContent = {
//       SwipeToDismissBackGround()
//         },
//  modifier = Modifier.animateItemPlacement()
//   ) {}


//@Composable
//private fun SwipeToDismissBackGround() {
//    Box(
//        modifier = Modifier
//            .padding(bottom = 12.dp)
//            .fillMaxWidth()
//            .height(200.dp)
//            .background(
//                color = animateColorAsState(targetValue = Color.Red).value,
//                shape = RoundedCornerShape(25.dp)
//            )
//            .padding(28.dp),
//        contentAlignment = Alignment.CenterEnd
//    ) {
//        Icon(
//            Icons.Default.Delete,
//            contentDescription = stringResource(R.string.delete_icon),
//            tint = Color.White
//        )
//    }
//}



