package com.example.weatherapp.data.model.five_days_weather_forecast.type_converter

import androidx.room.TypeConverter
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListOfWeatherItemTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromWeatherList(weather: List<WeatherItem>): String {
        return gson.toJson(weather)
    }

    @TypeConverter
    fun toWeatherList(weatherString: String): List<WeatherItem> {
        val type = object : TypeToken<List<WeatherItem>>() {}.type
        return Gson().fromJson(weatherString, type)
    }
}