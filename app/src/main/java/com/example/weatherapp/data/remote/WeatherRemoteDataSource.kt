package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FivedaysWeatherForecastResponse
import retrofit2.Response

class WeatherRemoteDataSource {

    private val apiService: ApiService = RetrofitFactory.apiService

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Response<CurrentWeatherResponse> {

        return apiService.getCurrentWeather(latitude = latitude, longitude = longitude)

    }

    suspend fun getFiveDaysWeatherForecast(
        latitude: Double,
        longitude: Double
    ): Response<FivedaysWeatherForecastResponse> {

        return apiService.getFiveDaysWeatherForecast(latitude = latitude, longitude = longitude)

    }
}