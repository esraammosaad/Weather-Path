package com.example.weatherapp.data.model.current_weather.type_converters

import androidx.room.TypeConverter
import com.example.weatherapp.data.model.current_weather.Coord
import com.google.gson.Gson

class CoordTypeConverter {
    private val gson = Gson()


    @TypeConverter
    fun convertToJsonString(coord: Coord): String {
        return gson.toJson(coord)
    }


    @TypeConverter
    fun convertToObject(json: String?): Coord {
        return gson.fromJson(json, Coord::class.java)
    }
}