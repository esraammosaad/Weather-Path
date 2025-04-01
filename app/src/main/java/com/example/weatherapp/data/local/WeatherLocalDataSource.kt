package com.example.weatherapp.data.local

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.weatherapp.data.model.AlarmModel
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

    suspend fun updateCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Int {

        return weatherDao.updateCurrentWeather(currentWeatherResponse)
    }

    suspend fun updateFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Int {

        return weatherDao.updateFiveDaysWeather(fiveDaysWeatherForecastResponse)
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

    suspend fun selectFiveDaysWeather(
        longitude: Double,
        latitude: Double
    ): Flow<FiveDaysWeatherForecastResponse> {

        return weatherDao.selectFiveDaysWeather(longitude = longitude, latitude = latitude)
    }

    suspend fun selectDayWeather(
        longitude: Double,
        latitude: Double
    ): Flow<CurrentWeatherResponse> {

        return weatherDao.selectDayWeather(longitude = longitude, latitude = latitude)
    }

    suspend fun insertAlarm(alarm: AlarmModel): Long {

        return weatherDao.insertAlarm(alarm)
    }

    suspend fun deleteAlarm(locationId: Int): Int {
        return weatherDao.deleteAlarm(locationId)
    }

    suspend fun selectAllAlarms(): Flow<List<AlarmModel>> {

        return weatherDao.selectAllAlarms()
    }

    suspend fun updateAlarm(alarm: AlarmModel): Int {

        return weatherDao.updateAlarm(alarm)
    }

}