package com.example.weatherapp.data.model.current_weather.type_converters


import androidx.room.TypeConverter
import com.example.weatherapp.data.model.Weather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListOfWeatherTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromWeatherList(weather: List<Weather>): String {
        return gson.toJson(weather)
    }

    @TypeConverter
    fun toWeatherList(weatherString: String): List<Weather> {
        val type = object : TypeToken<List<Weather>>() {}.type
        return Gson().fromJson(weatherString, type)
    }
}