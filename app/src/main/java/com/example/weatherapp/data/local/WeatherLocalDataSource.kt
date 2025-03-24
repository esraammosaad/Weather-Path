package com.example.weatherapp.data.local

import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(private val weatherDao: WeatherDao) {


    suspend fun insertCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Long {

        return weatherDao.insertCurrentWeather(currentWeatherResponse)
    }
    suspend fun insertFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Long {

        return weatherDao.insertFiveDaysWeather(fiveDaysWeatherForecastResponse)
    }

    suspend fun deleteCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Int {

        return weatherDao.deleteCurrentWeather(currentWeatherResponse)
    }

    suspend fun deleteFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Int {

        return weatherDao.deleteFiveDaysWeather(fiveDaysWeatherForecastResponse)
    }

    suspend fun selectAllFavorites(): Flow<List<CurrentWeatherResponse>> {

        return weatherDao.selectAllFavorites()
    }

    suspend fun selectFiveDaysWeatherFromFavorites(): Flow<List<FiveDaysWeatherForecastResponse>> {

        return weatherDao.selectFiveDaysWeatherFromFavorites()
    }

    suspend fun selectFiveDaysWeather(longitude: Double, latitude: Double): FiveDaysWeatherForecastResponse {

        return weatherDao.selectFiveDaysWeather(longitude = longitude, latitude = latitude)
    }


}