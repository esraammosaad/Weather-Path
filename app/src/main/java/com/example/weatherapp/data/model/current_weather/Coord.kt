package com.example.weatherapp.data.model.current_weather

import androidx.room.TypeConverters


@TypeConverters(Coord::class)
data class Coord(
    val lat: Double,
    val lon: Double
)