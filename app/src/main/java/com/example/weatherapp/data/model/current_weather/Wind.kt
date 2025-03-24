package com.example.weatherapp.data.model.current_weather

import androidx.room.TypeConverters
import com.example.weatherapp.data.model.current_weather.type_converters.WindTypeConverter


@TypeConverters(WindTypeConverter::class)
data class Wind(
    val deg: Int,
    val gust: Double,
    val speed: Double
)