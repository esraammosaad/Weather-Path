package com.example.weatherapp.favorite.view.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.data.managers.WeatherWorkManager
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.calculateDelay
import com.example.weatherapp.utilis.view.RadioButtonSingleSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.ui.datetimepicker.WheelDateTimePickerComponent.WheelDateTimePicker
import network.chaintech.kmp_date_time_picker.utils.now
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateAndTimePickerForSet(
    showDatePicker: MutableState<Boolean>,
    title: MutableState<String>,
    selectedWeather: CurrentWeatherResponse?,
    snackBarHostState: SnackbarHostState,
    favoriteViewModel: FavoriteViewModelImpl,
    coroutineScope: CoroutineScope
) {
    val context = LocalContext.current
    val alarmType = remember{ mutableStateOf("Alert") }
    ModalBottomSheet(
        onDismissRequest = {},
        containerColor = OffWhite,

        ) {
        Column {
            WheelDateTimePicker(
                height = 200.dp,
                title = stringResource(R.string.choose_date_and_time),
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

                    onDoneDateAndTimePickerClicked(
                        selectedWeather,
                        snappedDate,
                        alarmType,
                        favoriteViewModel,
                        coroutineScope,
                        snackBarHostState,
                        context,
                        showDatePicker
                    )
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Would you like to receive weather updates for ${title.value} via alerts or notifications?",
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

            RadioButtonSingleSelection({
                selectedItem ->alarmType.value= selectedItem.ifEmpty { "Alert" }
                if(selectedItem== context.getString(R.string.notification)){

//                    askForNotificationPermission(context)

                }
            }, listOf(stringResource(R.string.alert), stringResource(R.string.notification)))
        }
    }
}
//private fun askForNotificationPermission(context: Context) {
//    if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
//        val notificationManager = context.getSystemService(NotificationManager::class.java)
//        if (!notificationManager.areNotificationsEnabled()) {
//            ActivityCompat.requestPermissions(
//                MainActivity(),
//                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                10
//            )
//        }
//    }
//}

private fun onDoneDateAndTimePickerClicked(
    selectedWeather: CurrentWeatherResponse?,
    snappedDate: LocalDateTime,
    alarmType: MutableState<String>,
    favoriteViewModel: FavoriteViewModelImpl,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    context: Context,
    showDatePicker: MutableState<Boolean>
) {
    val alarm = AlarmModel(
        locationId = selectedWeather?.id ?: 0,
        date = "${snappedDate.dayOfWeek.name} ${snappedDate.dayOfMonth} ${snappedDate.month.name} ${snappedDate.year}",
        time = "${snappedDate.hour}:${snappedDate.minute}",
        countryName = selectedWeather?.countryName ?: "",
        cityName = selectedWeather?.cityName ?: "",
        alarmType = alarmType.value,
        longitude = selectedWeather?.longitude?: 0.0,
        latitude = selectedWeather?.latitude?:0.0
    )
    val duration = calculateDelay(
        targetYear = snappedDate.year,
        targetMonth = snappedDate.monthNumber,
        targetDay = snappedDate.dayOfMonth,
        targetHour = snappedDate.hour,
        targetMinute = snappedDate.minute,
    )
    favoriteViewModel.insertAlarm(alarm)
    coroutineScope.launch {
        snackBarHostState.showSnackbar(context.getString(R.string.alarm_set_successfully))
    }
    requestWorkManagerForSet(selectedWeather, context, duration)
    showDatePicker.value = false
}

private fun requestWorkManagerForSet(
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
            .addTag(selectedWeather?.id.toString())
            .build()
    WorkManager.getInstance(context).enqueue(workRequest)
}