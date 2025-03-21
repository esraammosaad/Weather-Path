package com.example.weatherapp.data.model.five_days_weather_forecast

data class FivedaysWeatherForecastResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<Item0>,
    val message: Int
)