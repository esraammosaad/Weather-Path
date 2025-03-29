package com.example.weatherapp.alarm.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.calculateDelay
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.ui.datetimepicker.WheelDateTimePickerComponent.WheelDateTimePicker
import network.chaintech.kmp_date_time_picker.ui.datetimepicker.WheelDateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.now


@Composable
fun AlarmScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Hello From Alarm")
        WheelDateTimePicker(
//            showDatePicker = true,
            height = 200.dp,
            title = "Set a weather alarm for Mansoura",
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
            onDoneClick = {

//                snappedDate ->
//                selectedTime.value = "${snappedDate.hour}:${snappedDate.minute}"
//                selectedDate.value =
//                    "${snappedDate.dayOfWeek.name} ${snappedDate.dayOfMonth} ${snappedDate.month.name} ${snappedDate.year}"
//                val duration = calculateDelay(
//                    targetYear = snappedDate.year,
//                    targetMonth = snappedDate.monthNumber,
//                    targetDay = snappedDate.dayOfMonth,
//                    targetHour = snappedDate.hour,
//                    targetMinute = snappedDate.minute,
//                )
//                requestWorkManager(selectedWeather, context, duration)
//                showDatePicker.value = false
            },
//            containerColor = OffWhite,

            )


    }

}