package com.example.weatherapp.data.model.five_days_weather_forecast.type_converter

import androidx.room.TypeConverter
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.google.gson.Gson

class FiveDaysWeatherForecastTypeConverter {

    private val gson = Gson()


    @TypeConverter
    fun convertToJsonString(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): String {
        return gson.toJson(fiveDaysWeatherForecastResponse)
    }


    @TypeConverter
    fun convertToObject(json: String?): FiveDaysWeatherForecastResponse {
        return gson.fromJson(json, FiveDaysWeatherForecastResponse::class.java)
    }


}