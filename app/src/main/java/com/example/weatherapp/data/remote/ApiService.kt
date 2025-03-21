package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FivedaysWeatherForecastResponse
import com.example.weatherapp.utilis.Strings
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query



interface ApiService {


    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("appid") apiKey: String= Strings.API_KEY,
        @Query("lon") longitude: Double,
        @Query("lat") latitude: Double,
        @Query("units") unit:String="standard",
        @Query("lang") language:String="en"
    ): Response<CurrentWeatherResponse>



    @GET("forecast")
    suspend fun getFiveDaysWeatherForecast(
        @Query("appid") apiKey: String= Strings.API_KEY,
        @Query("lon") longitude: Double,
        @Query("lat") latitude: Double,
        @Query("units") unit:String="standard",
        @Query("lang") language:String="en"
    ): Response<FivedaysWeatherForecastResponse>


}