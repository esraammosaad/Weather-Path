package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow


class WeatherRemoteDataSource(private val apiService: ApiService = RetrofitFactory.apiService) {

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): CurrentWeatherResponse {
        return apiService.getCurrentWeather(
            latitude = latitude,
            longitude = longitude
        )
    }

    suspend fun getFiveDaysWeatherForecast(
        latitude: Double,
        longitude: Double
    ): Flow<WeatherItem> {
        return apiService.getFiveDaysWeatherForecast(
            latitude = latitude,
            longitude = longitude
        ).list.asFlow()
    }
}