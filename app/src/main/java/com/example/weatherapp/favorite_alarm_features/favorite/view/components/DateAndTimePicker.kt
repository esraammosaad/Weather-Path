package com.example.weatherapp.favorite_alarm_features.favorite.view.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.data.managers.WorkManagerHelper
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteAndAlarmSharedViewModelImpl
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.utilis.Styles
import com.example.weatherapp.utilis.view.RadioButtonSingleSelection
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.ui.datetimepicker.WheelDateTimePickerComponent.WheelDateTimePicker
import network.chaintech.kmp_date_time_picker.utils.TimeFormat
import network.chaintech.kmp_date_time_picker.utils.now

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateAndTimePickerForSet(
    showDatePicker: MutableState<Boolean>,
    title: MutableState<String>,
    selectedWeather: CurrentWeatherResponse?,
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl,
) {
    val context = LocalContext.current
    val alarmType = remember { mutableStateOf("Alert") }
    val alarmState = remember { mutableStateOf(context.getString(R.string.alert)) }
    ModalBottomSheet(
        onDismissRequest = {
            showDatePicker.value = false

        },
        containerColor = OffWhite,

        ) {
        Column {
            WheelDateTimePicker(
                height = 200.dp,
                title = stringResource(R.string.choose_date_and_time),
                rowCount = 3,
                minDateTime = LocalDateTime.now(),
                titleStyle = Styles.textStyleNormal16,
                doneLabelStyle = Styles.textStyleNormal14,
                doneLabel = stringResource(R.string.done),
                timeFormat = TimeFormat.AM_PM,
                onDoneClick = { snappedDate ->
                    onDoneDateAndTimePickerClicked(
                        selectedWeather,
                        snappedDate,
                        alarmType,
                        favoriteViewModel,
                        context,
                        showDatePicker
                    )
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                stringResource(
                    R.string.would_you_like_to_receive_weather_updates_for_via_alerts_or_notifications,
                    title.value
                ),
                style = Styles.textStyleNormal16,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.choose_your_preferred_option_to_stay_informed),
                style = Styles.textStyleSemiBold15,
                color = PrimaryColor,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Column(modifier = Modifier.padding(horizontal = 10.dp)) {

                RadioButtonSingleSelection(
                    { selectedItem ->
                        alarmType.value =
                            if (selectedItem == context.getString(R.string.notification)) "Notification" else "Alert"
                        alarmState.value = selectedItem
                    },
                    listOf(stringResource(R.string.alert), stringResource(R.string.notification)),
                    alarmState.value
                )
            }
        }
    }
}


private fun onDoneDateAndTimePickerClicked(
    selectedWeather: CurrentWeatherResponse?,
    snappedDate: LocalDateTime,
    alarmType: MutableState<String>,
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl,
    context: Context,
    showDatePicker: MutableState<Boolean>
) {
    val alarm = AlarmModel(
        locationId = selectedWeather?.id ?: 0,
        date = "${snappedDate.date}",
        time = "${snappedDate.time}",
        countryName = selectedWeather?.countryName ?: "",
        cityName = selectedWeather?.cityName ?: "",
        alarmType = alarmType.value,
        longitude = selectedWeather?.longitude ?: 0.0,
        latitude = selectedWeather?.latitude ?: 0.0
    )
    val duration = favoriteViewModel.calculateDelay(
        targetYear = snappedDate.year,
        targetMonth = snappedDate.monthNumber,
        targetDay = snappedDate.dayOfMonth,
        targetHour = snappedDate.hour,
        targetMinute = snappedDate.minute,
    )
    favoriteViewModel.insertAlarm(alarm)
    WorkManagerHelper.requestWorkManagerForSet(selectedWeather, context, duration)
    showDatePicker.value = false
}

