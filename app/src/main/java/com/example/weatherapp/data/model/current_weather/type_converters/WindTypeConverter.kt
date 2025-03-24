package com.example.weatherapp.data.model.current_weather.type_converters

import androidx.room.TypeConverter
import com.example.weatherapp.data.model.current_weather.Wind
import com.google.gson.Gson

class WindTypeConverter {
    private val gson = Gson()


    @TypeConverter
    fun convertToJsonString(wind : Wind): String {
        return gson.toJson(wind)
    }


    @TypeConverter
    fun convertToObject(json: String?): Wind {
        return gson.fromJson(json, Wind::class.java)
    }
}