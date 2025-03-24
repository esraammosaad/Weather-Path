package com.example.weatherapp.data.model.current_weather.type_converters

import androidx.room.TypeConverter
import com.example.weatherapp.data.model.current_weather.Sys
import com.google.gson.Gson

class SysTypeConverter {
    private val gson = Gson()


    @TypeConverter
    fun convertToJsonString(sys: Sys): String {
        return gson.toJson(sys)
    }


    @TypeConverter
    fun convertToObject(json: String?): Sys {
        return gson.fromJson(json, Sys::class.java)
    }
}