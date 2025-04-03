package com.example.weatherapp.favorite.view_model

import android.location.Geocoder
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import kotlinx.coroutines.flow.StateFlow

interface FavoriteViewModel {
    var selectedWeather: StateFlow<Response>
    var selectedFiveDaysWeatherForecast: StateFlow<Response>
    var countryName: StateFlow<Response>
    var currentDayList: StateFlow<List<WeatherItem>>
    var nextDayList: StateFlow<List<WeatherItem>>
    var thirdDayList: StateFlow<List<WeatherItem>>
    var fourthDayList: StateFlow<List<WeatherItem>>
    var fifthDayList: StateFlow<List<WeatherItem>>
    var sixthDayList: StateFlow<List<WeatherItem>>
    var weatherFavorites: StateFlow<Response>
    var fiveDaysForecastFavorites: StateFlow<Response>
    var message: StateFlow<Int>
    var alarms: StateFlow<Response>
    fun getSelectedWeather(
        latitude: Double, longitude: Double, isConnected: Boolean, languageCode: String,
        tempUnit: String
    )

    fun getSelectedFiveDaysWeatherForecast(
        latitude: Double,
        longitude: Double,
        isConnected: Boolean,
        languageCode: String,
        tempUnit: String
    )

    fun insertWeather(
        currentWeatherResponse: CurrentWeatherResponse,
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    )

    fun updateWeather(
        currentWeatherResponse: CurrentWeatherResponse,
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    )

    fun updateSelectedWeather(
        currentWeatherResponse: CurrentWeatherResponse
    )

    fun getCountryName(
        longitude: Double,
        latitude: Double,
        geocoder: Geocoder,
        isConnected: Boolean
    )

    fun deleteWeather(
        currentWeatherResponse: CurrentWeatherResponse,
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    )

    fun selectFavorites()
    fun selectAllAlarms()
    fun insertAlarm(alarm: AlarmModel)
    fun deleteAlarm(locationId: Int)
    fun updateAlarm(alarm: AlarmModel)
    fun calculateDelay(
        targetYear: Int,
        targetMonth: Int,
        targetDay: Int,
        targetHour: Int,
        targetMinute: Int
    ): Long
}