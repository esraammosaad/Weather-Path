package com.example.weatherapp.data.model.current_weather

import androidx.room.TypeConverters
import com.example.weatherapp.data.model.current_weather.type_converters.MainTypeConverter


@TypeConverters(MainTypeConverter::class)
data class Main(
    val feels_like: Double,
    val grnd_level: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)