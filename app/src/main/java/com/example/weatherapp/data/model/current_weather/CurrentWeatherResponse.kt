package com.example.weatherapp.data.model.current_weather

import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.weatherapp.data.model.Weather
import com.example.weatherapp.data.model.current_weather.type_converters.ListOfWeatherTypeConverter
import java.io.Serializable


@Entity(tableName = "current_weather_table", primaryKeys = ["longitude", "latitude"])
data class CurrentWeatherResponse(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    @TypeConverters(ListOfWeatherTypeConverter::class)
    val weather: List<Weather>,
    val wind: Wind,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var countryName:String="",
    var cityName:String=""
)