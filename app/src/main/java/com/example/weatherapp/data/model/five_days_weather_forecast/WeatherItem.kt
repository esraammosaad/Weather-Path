package com.example.weatherapp.data.model.five_days_weather_forecast

import androidx.room.TypeConverters
import com.example.weatherapp.data.model.Weather
import com.example.weatherapp.data.model.current_weather.Clouds
import com.example.weatherapp.data.model.current_weather.Main
import com.example.weatherapp.data.model.current_weather.Wind
import com.example.weatherapp.data.model.current_weather.type_converters.CloudsTypeConverter
import com.example.weatherapp.data.model.current_weather.type_converters.ListOfWeatherTypeConverter
import com.example.weatherapp.data.model.current_weather.type_converters.WindTypeConverter
import com.example.weatherapp.data.model.five_days_weather_forecast.type_converter.WeatherItemTypeConverter


@TypeConverters(WeatherItemTypeConverter::class)
data class WeatherItem(
    @TypeConverters(CloudsTypeConverter::class)
    val clouds: Clouds,
    val dt: Int,
    val dt_txt: String,
    @TypeConverters(Main::class)
    val main: Main,
    val pop: Double,
    val visibility: Int,
    @TypeConverters(ListOfWeatherTypeConverter::class)
    val weather: List<Weather>,
    @TypeConverters(WindTypeConverter::class)
    val wind: Wind
)