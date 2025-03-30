package com.example.weatherapp.favorite.view.components

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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.data.model.AlarmModel

@Composable
fun CustomDateAndTimeOfAlarm(selectedAlarm: AlarmModel?) {
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
                selectedAlarm?.time
                    ?: stringResource(R.string.no_alarm_set),
                fontSize = 16.sp
            )
            Spacer(Modifier.height(5.dp))
            Text(
                selectedAlarm?.date
                    ?: stringResource(R.string.stay_ready),
                fontSize = 15.sp
            )
        }
    }
}