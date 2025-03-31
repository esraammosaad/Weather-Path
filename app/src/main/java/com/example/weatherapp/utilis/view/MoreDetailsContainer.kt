package com.example.weatherapp.utilis.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.utilis.convertUnixToTime

@Composable
fun MoreDetailsContainer(currentWeather: CurrentWeatherResponse) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {

        Box(
            Modifier
                .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(15.dp))
                .fillMaxWidth()
                .height(190.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(end = 20.dp)) {
                    MoreDetailsItem(
                        icon = R.drawable.sunrise,
                        textOne = stringResource(R.string.sunrise),
                        textTwo = convertUnixToTime(currentWeather.sys.sunrise.toLong())
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    MoreDetailsItem(
                        icon = R.drawable.wind,
                        textOne = stringResource(R.string.wind),
                        textTwo = currentWeather.wind.speed.toString() + stringResource(R.string.m_s)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    MoreDetailsItem(
                        icon = R.drawable.thermometer,
                        textOne = stringResource(R.string.pressure),
                        textTwo = currentWeather.main.pressure.toString() + stringResource(R.string.hpa)
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                VerticalDivider(
                    modifier = Modifier
                        .height(180.dp)
                        .width(1.dp), color = Color.Gray.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Column(modifier = Modifier.padding(start = 20.dp)) {
                    MoreDetailsItem(
                        icon = R.drawable.sunset,
                        textOne = stringResource(R.string.sunset),
                        textTwo = convertUnixToTime(currentWeather.sys.sunset.toLong())
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    MoreDetailsItem(
                        icon = R.drawable.humidity,
                        textOne = stringResource(R.string.humidity),
                        textTwo = currentWeather.main.humidity.toString() + " %"
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    MoreDetailsItem(
                        icon = R.drawable.cloud_sketched,
                        textOne = stringResource(R.string.clouds),
                        textTwo = currentWeather.clouds.all.toString() + " %"
                    )
                }
            }
        }
    }
}