package com.example.weatherapp.data.model.current_weather

import androidx.room.TypeConverters
import com.example.weatherapp.data.model.current_weather.type_converters.CloudsTypeConverter


@TypeConverters(CloudsTypeConverter::class)
data class Clouds(
    val all: Int
)