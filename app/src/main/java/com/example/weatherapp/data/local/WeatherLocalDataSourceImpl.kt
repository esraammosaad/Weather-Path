package com.example.weatherapp.data.local

import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(private val weatherDao: WeatherDao) : WeatherLocalDataSource {


    override suspend fun insertCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Long {

        return weatherDao.insertCurrentWeather(currentWeatherResponse)
    }

    override suspend fun insertFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Long {

        return weatherDao.insertFiveDaysWeather(fiveDaysWeatherForecastResponse)
    }

    override suspend fun updateCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Int {

        return weatherDao.updateCurrentWeather(currentWeatherResponse)
    }

    override suspend fun updateFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Int {

        return weatherDao.updateFiveDaysWeather(fiveDaysWeatherForecastResponse)
    }

    override suspend fun deleteCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Int {

        return weatherDao.deleteCurrentWeather(currentWeatherResponse)
    }

    override suspend fun deleteFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Int {

        return weatherDao.deleteFiveDaysWeather(fiveDaysWeatherForecastResponse)
    }

    override suspend fun selectAllFavorites(): Flow<List<CurrentWeatherResponse>> {

        return weatherDao.selectAllFavorites()
    }

    override suspend fun selectFiveDaysWeatherFromFavorites(): Flow<List<FiveDaysWeatherForecastResponse>> {

        return weatherDao.selectFiveDaysWeatherFromFavorites()
    }

    override suspend fun selectFiveDaysWeather(
        longitude: Double,
        latitude: Double
    ): Flow<FiveDaysWeatherForecastResponse> {

        return weatherDao.selectFiveDaysWeather(longitude = longitude, latitude = latitude)
    }

    override suspend fun selectDayWeather(
        longitude: Double,
        latitude: Double
    ): Flow<CurrentWeatherResponse> {

        return weatherDao.selectDayWeather(longitude = longitude, latitude = latitude)
    }

    override suspend fun insertAlarm(alarm: AlarmModel): Long {

        return weatherDao.insertAlarm(alarm)
    }

    override suspend fun deleteAlarm(locationId: Int): Int {
        return weatherDao.deleteAlarm(locationId)
    }

    override suspend fun selectAllAlarms(): Flow<List<AlarmModel>> {

        return weatherDao.selectAllAlarms()
    }

    override suspend fun updateAlarm(alarm: AlarmModel): Int {

        return weatherDao.updateAlarm(alarm)
    }

}