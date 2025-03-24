package com.example.weatherapp.data.model.current_weather.type_converters

import androidx.room.TypeConverter
import com.example.weatherapp.data.model.current_weather.Clouds
import com.google.gson.Gson

class CloudsTypeConverter {
    private val gson = Gson()


    @TypeConverter
    fun convertToJsonString(clouds: Clouds): String {
        return gson.toJson(clouds)
    }


    @TypeConverter
    fun convertToObject(json: String?): Clouds {
        return gson.fromJson(json, Clouds::class.java)
    }
}