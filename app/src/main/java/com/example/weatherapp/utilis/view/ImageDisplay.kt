package com.example.weatherapp.utilis.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.utilis.getWeatherImage
import com.example.weatherapp.utilis.isMorning

@Composable
fun ImageDisplay(currentWeatherResponse: CurrentWeatherResponse) {
    Image(
        painter = painterResource(
            getWeatherImage(currentWeatherResponse.weather.firstOrNull()?.icon?:"")
        ),
        contentDescription = stringResource(R.string.sun_or_moon_icon),
        modifier = Modifier
            .size(150.dp)
            .padding(top = 36.dp, end = 16.dp)
    )
}
