package com.example.weatherapp.data.repository

import android.location.Address
import android.location.Geocoder
import com.example.weatherapp.data.local.WeatherLocalDataSourceImpl
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
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

    suspend fun getCountryName(
        longitude: Double,
        latitude: Double,
        geocoder: Geocoder,
    ): MutableList<Address>?

    suspend fun insertCurrentWeather(
        currentWeatherResponse: CurrentWeatherResponse
    ): Long

    suspend fun insertFiveDaysWeather(
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    ): Long

    suspend fun updateCurrentWeather(
        currentWeatherResponse: CurrentWeatherResponse
    ): Int

    suspend fun updateFiveDaysWeather(
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    ): Int

    suspend fun deleteCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Int

    suspend fun deleteFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Int

    suspend fun selectDayWeather(
        longitude: Double,
        latitude: Double
    ): Flow<CurrentWeatherResponse>

    suspend fun selectFiveDaysWeather(
        longitude: Double,
        latitude: Double
    ): Flow<FiveDaysWeatherForecastResponse>

    suspend fun selectAllFavorites(): Flow<List<CurrentWeatherResponse>>

    suspend fun selectAllFiveDaysWeatherFromFavorites(): Flow<List<FiveDaysWeatherForecastResponse>>

    suspend fun insertAlarm(alarm: AlarmModel): Long

    suspend fun deleteAlarm(locationId: Int): Int

    suspend fun selectAllAlarms(): Flow<List<AlarmModel>>

    suspend fun updateAlarm(alarm: AlarmModel): Int

}