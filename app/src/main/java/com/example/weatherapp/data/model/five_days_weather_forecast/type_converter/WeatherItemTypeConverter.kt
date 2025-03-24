package com.example.weatherapp.data.model.five_days_weather_forecast.type_converter

import androidx.room.TypeConverter
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.google.gson.Gson

class WeatherItemTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun convertToJsonString(weatherItem: WeatherItem): String {
        return gson.toJson(weatherItem)
    }


    @TypeConverter
    fun convertToObject(json: String?): WeatherItem {
        return gson.fromJson(json, WeatherItem::class.java)
    }
}