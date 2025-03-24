package com.example.weatherapp.data.model.five_days_weather_forecast

import androidx.room.TypeConverters
import com.example.weatherapp.data.model.current_weather.Coord
import com.example.weatherapp.data.model.five_days_weather_forecast.type_converter.CityTypeConverter


@TypeConverters(CityTypeConverter::class)
data class City(
    val coord: Coord,
    val country: String,
    val id: Int,
    val name: String,
    val population: Int,
    val sunrise: Int,
    val sunset: Int,
    val timezone: Int
)