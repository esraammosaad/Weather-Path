package com.example.weatherapp.favorite_alarm_features.alarm.components

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
import com.example.weatherapp.R
import com.example.weatherapp.data.managers.WorkManagerHelper
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteAndAlarmSharedViewModelImpl
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.view.RadioButtonSingleSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.ui.datetimepicker.WheelDateTimePickerComponent.WheelDateTimePicker
import network.chaintech.kmp_date_time_picker.utils.now

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateAndTimePickerForUpdate(
    showDatePicker: MutableState<Boolean>,
    title: MutableState<String>,
    selectedAlarm: AlarmModel?,
    snackBarHostState: SnackbarHostState,
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl,
    coroutineScope: CoroutineScope
) {
    val context = LocalContext.current
    val alarmType = remember { mutableStateOf("Alert") }
    val alarmState = remember { mutableStateOf(context.getString(R.string.alert)) }
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
                    onDateAndPickerDoneClicked(
                        selectedAlarm,
                        favoriteViewModel,
                        snappedDate,
                        alarmType,
                        coroutineScope,
                        snackBarHostState,
                        context,
                        showDatePicker
                    )

                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.would_you_like_to_receive_weather_updates_for) + " " + title.value + " " + stringResource(
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
            RadioButtonSingleSelection(    { selectedItem ->
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

private fun onDateAndPickerDoneClicked(
    selectedAlarm: AlarmModel?,
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl,
    snappedDate: LocalDateTime,
    alarmType: MutableState<String>,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    context: Context,
    showDatePicker: MutableState<Boolean>
) {
    if (selectedAlarm != null) {
        val duration = favoriteViewModel.calculateDelay(
            targetYear = snappedDate.year,
            targetMonth = snappedDate.monthNumber,
            targetDay = snappedDate.dayOfMonth,
            targetHour = snappedDate.hour,
            targetMinute = snappedDate.minute,
        )
        selectedAlarm.date =
            "${snappedDate.dayOfWeek.name} ${snappedDate.dayOfMonth} ${snappedDate.month.name} ${snappedDate.year}"
        selectedAlarm.time = "${snappedDate.hour}:${snappedDate.minute}"
        selectedAlarm.alarmType = alarmType.value
        favoriteViewModel.updateAlarm(selectedAlarm)
        coroutineScope.launch {
            snackBarHostState.showSnackbar(context.getString(R.string.alarm_set_successfully))
        }
        WorkManagerHelper.requestWorkManagerForUpdate(selectedAlarm, context, duration)
        showDatePicker.value = false
    }
}