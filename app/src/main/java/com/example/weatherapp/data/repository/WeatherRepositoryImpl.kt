package com.example.weatherapp.data.repository

import android.location.Address
import android.location.Geocoder
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.local.WeatherLocalDataSourceImpl
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val weatherRemoteDataSourceImpl: WeatherRemoteDataSource,
    private val weatherLocalDataSourceImpl: WeatherLocalDataSource
) : WeatherRepository {

    companion object {
        private var instance: WeatherRepositoryImpl? = null
        fun getInstance(
            weatherRemoteDataSourceImpl: WeatherRemoteDataSourceImpl,
            weatherLocalDataSourceImpl: WeatherLocalDataSourceImpl
        ): WeatherRepositoryImpl {
            return instance ?: synchronized(this) {
                val temp =
                    WeatherRepositoryImpl(weatherRemoteDataSourceImpl, weatherLocalDataSourceImpl)
                instance = temp
                temp
            }
        }
    }

    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        languageCode: String,
        tempUnit: String
    ): CurrentWeatherResponse {
        return weatherRemoteDataSourceImpl.getCurrentWeather(
            latitude = latitude,
            longitude = longitude,
            languageCode = languageCode,
            tempUnit = tempUnit
        )
    }

    override suspend fun getFiveDaysWeatherForecast(
        latitude: Double,
        longitude: Double,
        languageCode: String,
        tempUnit: String
    ): Flow<WeatherItem> {
        return weatherRemoteDataSourceImpl.getFiveDaysWeatherForecast(
            latitude = latitude,
            longitude = longitude,
            languageCode = languageCode,
            tempUnit = tempUnit
        )
    }

    override suspend fun getCountryName(
        longitude: Double,
        latitude: Double,
        geocoder: Geocoder,
    ): MutableList<Address>? {

        return geocoder.getFromLocation(latitude, longitude, 1)
    }

    override suspend fun insertCurrentWeather(
        currentWeatherResponse: CurrentWeatherResponse
    ): Long {
        return weatherLocalDataSourceImpl.insertCurrentWeather(
            currentWeatherResponse
        )
    }

    override suspend fun insertFiveDaysWeather(
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    ): Long {
        return weatherLocalDataSourceImpl.insertFiveDaysWeather(
            fiveDaysWeatherForecastResponse
        )
    }

    override suspend fun updateCurrentWeather(
        currentWeatherResponse: CurrentWeatherResponse
    ): Int {
        return weatherLocalDataSourceImpl.updateCurrentWeather(
            currentWeatherResponse
        )
    }

    override suspend fun updateFiveDaysWeather(
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    ): Int {
        return weatherLocalDataSourceImpl.updateFiveDaysWeather(
            fiveDaysWeatherForecastResponse
        )
    }

    override suspend fun deleteCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Int {

        return weatherLocalDataSourceImpl.deleteCurrentWeather(currentWeatherResponse)
    }

    override suspend fun deleteFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Int {

        return weatherLocalDataSourceImpl.deleteFiveDaysWeather(fiveDaysWeatherForecastResponse)
    }

    override suspend fun selectDayWeather(
        longitude: Double,
        latitude: Double
    ): Flow<CurrentWeatherResponse> {

        return weatherLocalDataSourceImpl.selectDayWeather(
            longitude = longitude,
            latitude = latitude
        )
    }

    override suspend fun selectFiveDaysWeather(
        longitude: Double,
        latitude: Double
    ): Flow<FiveDaysWeatherForecastResponse> {

        return weatherLocalDataSourceImpl.selectFiveDaysWeather(
            longitude = longitude,
            latitude = latitude
        )
    }

    override suspend fun selectAllFavorites(): Flow<List<CurrentWeatherResponse>> {

        return weatherLocalDataSourceImpl.selectAllFavorites()
    }

    override suspend fun selectAllFiveDaysWeatherFromFavorites(): Flow<List<FiveDaysWeatherForecastResponse>> {

        return weatherLocalDataSourceImpl.selectFiveDaysWeatherFromFavorites()
    }


    override suspend fun insertAlarm(alarm: AlarmModel): Long {

        return weatherLocalDataSourceImpl.insertAlarm(alarm)
    }

    override suspend fun deleteAlarm(locationId: Int): Int {
        return weatherLocalDataSourceImpl.deleteAlarm(locationId)
    }

    override suspend fun selectAllAlarms(): Flow<List<AlarmModel>> {

        return weatherLocalDataSourceImpl.selectAllAlarms()
    }

    override suspend fun updateAlarm(alarm: AlarmModel): Int {

        return weatherLocalDataSourceImpl.updateAlarm(alarm)
    }

}