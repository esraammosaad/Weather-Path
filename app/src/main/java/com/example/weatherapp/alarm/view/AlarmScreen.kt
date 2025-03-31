package com.example.weatherapp.alarm.view

import android.content.Context
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
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
import com.example.weatherapp.R
import com.example.weatherapp.data.managers.WeatherWorkManager
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.favorite.view.screens.deleteAlarm
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.Styles
import com.example.weatherapp.utilis.calculateDelay
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.ConfirmationDialog
import com.example.weatherapp.utilis.view.FailureDisplay
import com.example.weatherapp.utilis.view.LoadingDisplay
import com.example.weatherapp.utilis.view.RadioButtonSingleSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.ui.datetimepicker.WheelDateTimePickerComponent.WheelDateTimePicker
import network.chaintech.kmp_date_time_picker.utils.now
import java.util.concurrent.TimeUnit

@Composable
fun AlarmScreen(
    favoriteViewModel: FavoriteViewModelImpl,
    currentWeather: CurrentWeatherResponse,
    snackBarHostState: SnackbarHostState,
    navigationBarViewModel: BottomNavigationBarViewModel
) {
    LaunchedEffect(Unit) {
        favoriteViewModel.selectAllAlarms()
    }
    val alarms by remember { favoriteViewModel.alarms }.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isDialog = remember { mutableStateOf(false) }
    val showDatePicker = remember { mutableStateOf(false) }
    val datePickerTitle = remember { mutableStateOf("") }
    val dialogTitle = remember { mutableStateOf("") }
    val dialogText = remember { mutableStateOf("") }
    val onConfirmation = remember { mutableStateOf({}) }
    navigationBarViewModel.setCurrentWeatherTheme(currentWeather.weather.firstOrNull()?.icon?:"")
    if (isDialog.value) {
        ConfirmationDialog(
            onConfirmation = {
                onConfirmation.value.invoke()
                isDialog.value = false
            },
            onDismiss = { isDialog.value = false },
            dialogTitle = dialogTitle.value,
            dialogText = dialogText.value,
        )
    }

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
                stringResource(R.string.alarms), textAlign = TextAlign.Center,
                style = Styles.textStyleSemiBold26
            )
        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp, end = 16.dp, start = 16.dp)) {
            when (alarms) {
                is Response.Failure -> item { FailureDisplay((alarms as Response.Failure).exception) }
                Response.Loading -> item { LoadingDisplay() }
                is Response.Success<*> -> {
                    val alarmsList = alarms as Response.Success<List<AlarmModel>>
                    items(alarmsList.result?.size ?: 0) { index ->
                        val item =   alarmsList.result?.get(index)
                        if (showDatePicker.value) {
                            DateAndTimePickerForUpdate(
                                showDatePicker = showDatePicker,
                                title = datePickerTitle,
                                selectedAlarm = item,
                                favoriteViewModel = favoriteViewModel,
                                snackBarHostState = snackBarHostState,
                                coroutineScope = coroutineScope
                            )
                        }

                        Box {
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 12.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .background(
                                            brush = getWeatherGradient(
                                                currentWeather.weather.firstOrNull()?.icon ?: ""
                                            ),
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
                                        modifier = Modifier.height(150.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column {
                                                Text(
                                                    text = item?.cityName
                                                        ?: stringResource(R.string.n_a),
                                                    style = Styles.textStyleSemiBold20,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.fillMaxWidth(0.6f)
                                                )
                                                Text(
                                                    text = item?.countryName
                                                        ?: stringResource(R.string.n_a),
                                                    style = Styles.textStyleNormal16
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Image(
                                                    painter = painterResource(R.drawable.notification),
                                                    contentDescription = stringResource(
                                                        R.string.notification_icon
                                                    ),
                                                    modifier = Modifier.size(25.dp)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Column(verticalArrangement = Arrangement.Center) {
                                                    Text(
                                                        item?.time
                                                            ?: stringResource(R.string.no_alarm_set),
                                                        fontSize = 16.sp
                                                    )
                                                    Spacer(Modifier.height(5.dp))
                                                    Text(
                                                        item?.date
                                                            ?: stringResource(R.string.stay_ready),
                                                        fontSize = 15.sp
                                                    )
                                                }
                                            }
                                            Switch(
                                                checked = true,
                                                onCheckedChange = {
                                                    isDialog.value = true
                                                    dialogTitle.value =
                                                        context.resources.getString(R.string.warning)
                                                    dialogText.value =
                                                        context.getString(R.string.you_sure_you_want_to_reset_the_alarm)
                                                    onConfirmation.value = {
                                                        deleteAlarm(
                                                            selectedAlarm = item,
                                                            favoriteViewModel = favoriteViewModel,
                                                            context = context,
                                                            coroutineScope = coroutineScope,
                                                            snackBarHostState = snackBarHostState
                                                        )
                                                        isDialog.value = false
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

//                                        Text(
//                                            text = item?.alarmType
//                                                ?: stringResource(R.string.n_a),
//                                            style = Styles.textStyleMedium16
//                                        )
                                    }

                                }
                            }
                            Icon(
                                painter = painterResource(R.drawable.baseline_edit_notifications_24),
                                contentDescription = stringResource(R.string.edit_alarm_icon),
                                tint = Color.White,
                                modifier = Modifier
                                    .align(alignment = Alignment.TopEnd)
                                    .padding(top = 5.dp, end = 10.dp)
                                    .size(26.dp)
                                    .clickable {
                                        showDatePicker.value = true
                                        datePickerTitle.value =
                                            item?.cityName ?: ""

                                    }
                            )

                        }


                    }


                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateAndTimePickerForUpdate(
    showDatePicker: MutableState<Boolean>,
    title: MutableState<String>,
    selectedAlarm: AlarmModel?,
    snackBarHostState: SnackbarHostState,
    favoriteViewModel: FavoriteViewModelImpl,
    coroutineScope: CoroutineScope
) {
    val context = LocalContext.current
    val alarmType = remember { mutableStateOf(context.getString(R.string.alert)) }
    ModalBottomSheet(
        onDismissRequest = {},
        containerColor = OffWhite,
    ) {
        Column {
            WheelDateTimePicker(
                height = 200.dp,
                title = stringResource(R.string.update_your_alarm),
                rowCount = 3,
                minDateTime = LocalDateTime.now(),
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
                    //update -- done
                    //clear work manager --
                    //set work manager again
                    if (selectedAlarm != null) {
                        val duration = calculateDelay(
                            targetYear = snappedDate.year,
                            targetMonth = snappedDate.monthNumber,
                            targetDay = snappedDate.dayOfMonth,
                            targetHour = snappedDate.hour,
                            targetMinute = snappedDate.minute,
                        )
                        selectedAlarm.date = "${snappedDate.dayOfWeek.name} ${snappedDate.dayOfMonth} ${snappedDate.month.name} ${snappedDate.year}"
                        selectedAlarm.time = "${snappedDate.hour}:${snappedDate.minute}"
                        selectedAlarm.alarmType = alarmType.value
                        favoriteViewModel.updateAlarm(selectedAlarm)
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(context.getString(R.string.alarm_set_successfully))
                        }
                        requestWorkManagerForUpdate(selectedAlarm, context, duration)
                        showDatePicker.value = false
                    }

                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                stringResource(R.string.would_you_like_to_receive_weather_updates_for)+ " "+title.value +" "+ stringResource(
                    R.string.via_alerts_or_notifications
                ),
                fontSize = 16.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.choose_your_preferred_option_to_stay_informed),
                fontSize = 15.sp,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            RadioButtonSingleSelection({ selectedItem ->
                alarmType.value = selectedItem.ifEmpty { context.getString(R.string.alert) }
            }, listOf(stringResource(R.string.alert), stringResource(R.string.notification)))

        }
    }


}

private fun requestWorkManagerForUpdate(
    selectedAlarm: AlarmModel?,
    context: Context,
    duration: Long
) {
    WorkManager.getInstance(context).cancelAllWorkByTag(selectedAlarm?.locationId.toString())
    val inputData = Data.Builder()
    inputData.putDouble(Strings.LONG_CONST, selectedAlarm?.longitude ?: 0.0)
    inputData.putDouble(Strings.LAT_CONST, selectedAlarm?.latitude ?: 0.0)
    inputData.putInt(Strings.CODE_CONST, selectedAlarm?.locationId ?: 0)
    val constraint = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    val workRequest: WorkRequest =
        OneTimeWorkRequestBuilder<WeatherWorkManager>().setConstraints(constraint)
            .setInputData(inputData.build()).setInitialDelay(duration, TimeUnit.MILLISECONDS)
            .addTag(selectedAlarm?.locationId.toString())
            .build()
    WorkManager.getInstance(context).enqueue(workRequest)
}

