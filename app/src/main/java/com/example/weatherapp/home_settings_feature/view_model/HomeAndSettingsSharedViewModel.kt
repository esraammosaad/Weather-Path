package com.example.weatherapp.home_settings_feature.view_model

import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import kotlinx.coroutines.flow.StateFlow

interface HomeAndSettingsSharedViewModel {
    var currentWeather: StateFlow<Response>
    var fiveDaysWeatherForecast: StateFlow<Response>
    var message: StateFlow<Int>
    var countryName: StateFlow<Response>
    var currentDayList: StateFlow<List<WeatherItem>>
    var nextDayList: StateFlow<List<WeatherItem>>
    var thirdDayList: StateFlow<List<WeatherItem>>
    var fourthDayList: StateFlow<List<WeatherItem>>
    var fifthDayList: StateFlow<List<WeatherItem>>
    var sixthDayList: StateFlow<List<WeatherItem>>
    fun getCurrentWeather(
        latitude: Double, longitude: Double, isConnected: Boolean, languageCode: String,
        tempUnit: String
    )

    fun getFiveDaysWeatherForecast(
        latitude: Double,
        longitude: Double,
        isConnected: Boolean,
        languageCode: String,
        tempUnit: String
    )

    fun getCountryName(
        longitude: Double,
        latitude: Double,
        geocoder: Geocoder,
        isConnected: Boolean
    )

    fun getWeatherFromApi(
        locationState: Location, geocoder: Geocoder, isConnected: Boolean, languageCode: String,
        tempUnit: String
    )

    fun insertFiveDaysForecast(
        fiveDaysWeatherForecast: List<WeatherItem>?,
        longitude: Double,
        latitude: Double
    )

    fun insertCurrentWeather(
        currentWeather: CurrentWeatherResponse?,
        countryName: Address?,
        longitude: Double,
        latitude: Double
    )
}