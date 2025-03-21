package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FivedaysWeatherForecastResponse
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import retrofit2.Response

class Repository(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val weatherLocalDataSource: WeatherLocalDataSource
) {

    companion object {
        private var instance: Repository? = null
        fun getInstance(
            weatherRemoteDataSource: WeatherRemoteDataSource,
            weatherLocalDataSource: WeatherLocalDataSource
        ): Repository {
            return instance ?: synchronized(this) {
                val temp =
                    Repository(weatherRemoteDataSource, weatherLocalDataSource)
                instance = temp
                temp
            }
        }


    }


    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Response<CurrentWeatherResponse> {

        return weatherRemoteDataSource.getCurrentWeather(latitude = latitude, longitude = longitude)
    }

    suspend fun getFiveDaysWeatherForecast(
        latitude: Double,
        longitude: Double
    ): Response<FivedaysWeatherForecastResponse> {


        return weatherRemoteDataSource.getFiveDaysWeatherForecast(
            latitude = latitude,
            longitude = longitude
        )


    }


}