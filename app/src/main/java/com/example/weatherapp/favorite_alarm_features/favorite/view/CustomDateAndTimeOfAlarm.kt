package com.example.weatherapp.favorite_alarm_features.favorite.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.utilis.convertToArabicNumbers

@Composable
fun CustomDateAndTimeOfAlarm(selectedAlarm: AlarmModel?) {
    val context = LocalContext.current
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
                convertToArabicNumbers( selectedAlarm?.time?:"", context).ifEmpty {
                    stringResource(R.string.no_alarm_set)},
                fontSize = 16.sp
            )
            Spacer(Modifier.height(3.dp))
            Text(
                convertToArabicNumbers( selectedAlarm?.date?:"",context).ifEmpty {
                     stringResource(R.string.stay_ready)},
                fontSize = 15.sp
            )
        }
    }
}