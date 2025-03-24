package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeatherResponse: CurrentWeatherResponse) : Long

    @Delete
    suspend fun deleteCurrentWeather(currentWeatherResponse: CurrentWeatherResponse) : Int

    @Query("SELECT * FROM current_weather_table")
    fun selectAllFavorites() : Flow<List<CurrentWeatherResponse>>


    @Query("SELECT * FROM current_weather_table WHERE longitude= :longitude AND latitude= :latitude")
    suspend fun selectDayWeather(longitude : Double , latitude: Double ) : CurrentWeatherResponse


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse) : Long

    @Delete
    suspend fun deleteFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse) : Int

    @Query("SELECT * FROM five_days_weather_table")
    fun selectFiveDaysWeatherFromFavorites() : Flow<List<FiveDaysWeatherForecastResponse>>


    @Query("SELECT * FROM five_days_weather_table WHERE longitude= :longitude AND latitude= :latitude")
    suspend fun selectFiveDaysWeather(longitude : Double , latitude: Double ) : FiveDaysWeatherForecastResponse




}