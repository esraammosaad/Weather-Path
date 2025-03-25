package com.example.weatherapp.utilis.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.formatTime

@Composable
fun DayWeatherItem(weatherItem: WeatherItem) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .padding(end = 10.dp)
                .size(60.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            WeatherStatusImageDisplay(
                weatherItem.weather[0].icon
            )
            Text(
                text = "${weatherItem.main.temp_min.toInt()}${Strings.CELSIUS_SYMBOL}",
                fontSize = 14.sp,
                color = Color.White,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium
            )

        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = formatTime(weatherItem.dt_txt),
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            fontFamily = poppinsFontFamily,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(end = 9.dp)

        )

    }


}