package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class WeatherRemoteDataSourceImpl(private val apiService: ApiService = RetrofitFactory.apiService) :
    WeatherRemoteDataSource {

    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        languageCode : String,
        tempUnit : String
    ): Flow<CurrentWeatherResponse> {
        return flowOf( apiService.getCurrentWeather(
            latitude = latitude,
            longitude = longitude,
            language = languageCode ,
            unit = tempUnit
        ))
    }

    override suspend fun getFiveDaysWeatherForecast(
        latitude: Double,
        longitude: Double,
        languageCode : String,
        tempUnit : String
    ): Flow<List<WeatherItem>> {
        return flowOf(apiService.getFiveDaysWeatherForecast(
            latitude = latitude,
            longitude = longitude,
            language = languageCode,
            unit = tempUnit
        ).list)
    }
}