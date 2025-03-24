package com.example.weatherapp.data.model.current_weather.type_converters

import androidx.room.TypeConverter
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.google.gson.Gson

class CurrentWeatherResponseTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun convertToJsonString(currentWeatherResponse: CurrentWeatherResponse): String {
        return gson.toJson(currentWeatherResponse)
    }


    @TypeConverter
    fun convertToObject(json: String?): CurrentWeatherResponse {
        return gson.fromJson(json, CurrentWeatherResponse::class.java)
    }

}