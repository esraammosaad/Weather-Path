package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Long

    @Update
    suspend fun updateCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Int

    @Update
    suspend fun updateFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Int

    @Delete
    suspend fun deleteCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Int

    @Delete
    suspend fun deleteFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse): Int

    @Query("SELECT * FROM current_weather_table")
    fun selectAllFavorites(): Flow<List<CurrentWeatherResponse>>

    @Query("SELECT * FROM current_weather_table WHERE ROUND(longitude, 2) = ROUND(:longitude, 2) AND ROUND(latitude, 2) = ROUND(:latitude, 2)")
    fun selectDayWeather(longitude: Double, latitude: Double): Flow<CurrentWeatherResponse>

    @Query("SELECT * FROM five_days_weather_table")
    fun selectFiveDaysWeatherFromFavorites(): Flow<List<FiveDaysWeatherForecastResponse>>

    @Query("SELECT * FROM five_days_weather_table WHERE ROUND(longitude, 2) = ROUND(:longitude, 2) AND ROUND(latitude, 2) = ROUND(:latitude, 2)")
    fun selectFiveDaysWeather(
        longitude: Double,
        latitude: Double
    ): Flow<FiveDaysWeatherForecastResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmModel): Long

    @Delete
    suspend fun deleteAlarm(alarm: AlarmModel): Int

    @Query("SELECT * FROM alarm_weather_table")
    fun selectAllAlarms(): Flow<List<AlarmModel>>

    @Update
    suspend fun updateAlarm(alarm: AlarmModel): Int

}