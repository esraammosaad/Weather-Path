package com.example.weatherapp.data.model.five_days_weather_forecast

data class FiveDaysWeatherForecastResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<WeatherItem>,
    val message: Int
)