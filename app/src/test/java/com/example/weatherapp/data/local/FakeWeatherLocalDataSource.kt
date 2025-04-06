package com.example.weatherapp.data.local

import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.Clouds
import com.example.weatherapp.data.model.current_weather.Coord
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.current_weather.Main
import com.example.weatherapp.data.model.current_weather.Sys
import com.example.weatherapp.data.model.current_weather.Wind
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeWeatherLocalDataSource : WeatherLocalDataSource {

    private fun currentWeatherResponse(id: Int) = CurrentWeatherResponse(
        base = "90",
        clouds = clouds(),
        cod = 10,
        coord = Coord(lat = 0.0, lon = 0.0),
        dt = 0,
        id = id,
        main = main(),
        name = "",
        sys = Sys(country = "", sunrise = 0, sunset = 0),
        timezone = 0,
        visibility = 0,
        weather = listOf(),
        wind = wind(),
        latitude = 0.0,
        longitude = 0.0,
        countryName = "",
        cityName = ""
    )

    private fun main() = Main(
        feels_like = 0.0,
        grnd_level = 0,
        humidity = 0,
        pressure = 0,
        sea_level = 0,
        temp = 0.0,
        temp_max = 0.0,
        temp_min = 0.0
    )

    private fun clouds() = Clouds(all = 0)

    private fun wind() = Wind(deg = 0, gust = 0.0, speed = 0.0)

    private val listOfWeatherItems = mutableListOf(
        currentWeatherResponse(0),
        currentWeatherResponse(1),
        currentWeatherResponse(2)
    )

    override suspend fun insertCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Long {

        if (listOfWeatherItems.contains(currentWeatherResponse)) {
            return 0L
        } else {
            listOfWeatherItems.add(currentWeatherResponse)
            return 1L
        }
    }

    override suspend fun insertFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Long {
        TODO("Not yet implemented")
    }

    override suspend fun updateCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Int {
        if (listOfWeatherItems.contains(currentWeatherResponse)) {
            listOfWeatherItems.remove(currentWeatherResponse)
            return 1
        } else {
            return 0
        }
    }

    override suspend fun deleteFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Int {
        TODO("Not yet implemented")
    }

    override suspend fun selectAllFavorites(): Flow<List<CurrentWeatherResponse>> {
        return flowOf(listOfWeatherItems)
    }

    override suspend fun selectFiveDaysWeatherFromFavorites(): Flow<List<FiveDaysWeatherForecastResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun selectFiveDaysWeather(
        longitude: Double,
        latitude: Double
    ): Flow<FiveDaysWeatherForecastResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun selectDayWeather(
        longitude: Double,
        latitude: Double
    ): Flow<CurrentWeatherResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alarm: AlarmModel): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(locationId: Int): Int {
        TODO("Not yet implemented")
    }

    override suspend fun selectAllAlarms(): Flow<List<AlarmModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateAlarm(alarm: AlarmModel): Int {
        TODO("Not yet implemented")
    }
}