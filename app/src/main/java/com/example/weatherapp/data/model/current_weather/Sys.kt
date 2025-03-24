package com.example.weatherapp.data.model.current_weather

import androidx.room.TypeConverters
import com.example.weatherapp.data.model.current_weather.type_converters.SysTypeConverter


@TypeConverters(SysTypeConverter::class)
data class Sys(
    val country: String,
    val sunrise: Int,
    val sunset: Int
)