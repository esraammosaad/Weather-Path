package com.example.weatherapp.data.model.current_weather.type_converters

import androidx.room.TypeConverter
import com.example.weatherapp.data.model.current_weather.Main
import com.google.gson.Gson

class MainTypeConverter {
    private val gson = Gson()


    @TypeConverter
    fun convertToJsonString(main: Main): String {
        return gson.toJson(main)
    }


    @TypeConverter
    fun convertToObject(json: String?): Main {
        return gson.fromJson(json, Main::class.java)
    }
}