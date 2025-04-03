package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        languageCode: String,
        tempUnit: String
    ): CurrentWeatherResponse

    suspend fun getFiveDaysWeatherForecast(
        latitude: Double,
        longitude: Double,
        languageCode: String,
        tempUnit: String
    ): Flow<WeatherItem>
}