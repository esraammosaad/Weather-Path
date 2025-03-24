package com.example.weatherapp.data.model.current_weather.type_converters

import androidx.room.TypeConverter
import com.example.weatherapp.data.model.Weather
import com.google.gson.Gson

class WeatherTypeConverter {

    private val gson = Gson()


    @TypeConverter
    fun convertToJsonString(weather : Weather): String {
        return gson.toJson(weather)
    }


    @TypeConverter
    fun convertToObject(json: String?): Weather {
        return gson.fromJson(json, Weather::class.java)
    }
}


