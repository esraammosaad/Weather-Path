package com.example.weatherapp.data.model

import androidx.room.TypeConverters
import com.example.weatherapp.data.model.current_weather.type_converters.WeatherTypeConverter


@TypeConverters(WeatherTypeConverter::class)
data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)