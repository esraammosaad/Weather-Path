package com.example.weatherapp.data.model.five_days_weather_forecast.type_converter

import androidx.room.TypeConverter
import com.example.weatherapp.data.model.five_days_weather_forecast.City
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.google.gson.Gson

class CityTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun convertToJsonString(city: City): String {
        return gson.toJson(city)
    }


    @TypeConverter
    fun convertToObject(json: String?): City {
        return gson.fromJson(json, City::class.java)
    }
}