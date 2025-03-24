package com.example.weatherapp.data.model.five_days_weather_forecast

import androidx.room.Entity


@Entity(tableName = "five_days_weather_table", primaryKeys = ["longitude", "latitude"])
data class FiveDaysWeatherForecastResponse(
    var list: List<WeatherItem>,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)