package com.example.weatherapp.utilis.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.Strings

@Composable
fun TemperatureDisplay(currentWeather: CurrentWeatherResponse) {
    val context = LocalContext.current
    Row {
        Text(
            currentWeather.main.temp.toString(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 70.sp,
            fontFamily = poppinsFontFamily,
            modifier = Modifier.padding(start = 14.dp),
            color = Color.White
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            stringResource( LocalStorageDataSource.getInstance(context).getTempSymbol),
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            fontFamily = poppinsFontFamily,
            color = Color.White
        )
    }
}