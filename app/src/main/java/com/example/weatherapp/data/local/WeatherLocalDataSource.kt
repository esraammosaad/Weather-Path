package com.example.weatherapp.data.local

import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    suspend fun insertCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Long

    suspend fun insertFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Long

    suspend fun updateCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Int

    suspend fun updateFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Int

    suspend fun deleteCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Int

    suspend fun deleteFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Int

    suspend fun selectAllFavorites(): Flow<List<CurrentWeatherResponse>>

    suspend fun selectFiveDaysWeatherFromFavorites(): Flow<List<FiveDaysWeatherForecastResponse>>

    suspend fun selectFiveDaysWeather(
        longitude: Double,
        latitude: Double
    ): Flow<FiveDaysWeatherForecastResponse>

    suspend fun selectDayWeather(
        longitude: Double,
        latitude: Double
    ): Flow<CurrentWeatherResponse>

    suspend fun insertAlarm(alarm: AlarmModel): Long

    suspend fun deleteAlarm(locationId: Int): Int

    suspend fun selectAllAlarms(): Flow<List<AlarmModel>>

    suspend fun updateAlarm(alarm: AlarmModel): Int
}