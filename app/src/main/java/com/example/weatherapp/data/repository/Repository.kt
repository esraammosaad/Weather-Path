package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

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
    ): CurrentWeatherResponse{

        return weatherRemoteDataSource.getCurrentWeather(latitude = latitude, longitude = longitude)
    }

    suspend fun getFiveDaysWeatherForecast(
        latitude: Double,
        longitude: Double
    ): Flow<WeatherItem> {
        return weatherRemoteDataSource.getFiveDaysWeatherForecast(
            latitude = latitude,
            longitude = longitude
        )

    }

    suspend fun insertCurrentWeather(
        currentWeatherResponse: CurrentWeatherResponse
    ): Long {

        return weatherLocalDataSource.insertCurrentWeather(
            currentWeatherResponse
        )

    }

    suspend fun insertFiveDaysWeather(
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    ): Long {

        return weatherLocalDataSource.insertFiveDaysWeather(
            fiveDaysWeatherForecastResponse
        )

    }

    suspend fun deleteCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Int {

        return weatherLocalDataSource.deleteCurrentWeather(currentWeatherResponse)
    }

    suspend fun deleteFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Int {

        return weatherLocalDataSource.deleteFiveDaysWeather(fiveDaysWeatherForecastResponse)
    }

    suspend fun selectFiveDaysWeather(
        longitude: Double,
        latitude: Double
    ): FiveDaysWeatherForecastResponse {

        return weatherLocalDataSource.selectFiveDaysWeather(
            longitude = longitude,
            latitude = latitude
        )
    }

    suspend fun selectAllFavorites(): Flow<List<CurrentWeatherResponse>> {

        return weatherLocalDataSource.selectAllFavorites()
    }

    suspend fun selectFiveDaysWeatherFromFavorites(): Flow<List<FiveDaysWeatherForecastResponse>> {

        return weatherLocalDataSource.selectFiveDaysWeatherFromFavorites()
    }

    suspend fun selectDayWeather(longitude : Double , latitude: Double ) : CurrentWeatherResponse{

        return weatherLocalDataSource.selectDayWeather(longitude = longitude,latitude=latitude)
    }


}