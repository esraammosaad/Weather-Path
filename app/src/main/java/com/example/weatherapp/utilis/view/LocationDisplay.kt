package com.example.weatherapp.utilis.view

import android.location.Address
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.poppinsFontFamily

@Composable
fun LocationDisplay(countryName: Address, currentWeather: CurrentWeatherResponse) {

    Log.i("TAG", "LocationDisplay: ${currentWeather.countryName}")
    Text(
        if (currentWeather.cityName.isNullOrEmpty()) countryName.locality
            ?: currentWeather.name else currentWeather.cityName,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        fontFamily = poppinsFontFamily,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 4.dp)

    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
        if (currentWeather.countryName.isNullOrEmpty()) countryName.countryName
            ?: currentWeather.sys.country else currentWeather.countryName,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        fontFamily = poppinsFontFamily,
        color = OffWhite,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 4.dp)


    )

}