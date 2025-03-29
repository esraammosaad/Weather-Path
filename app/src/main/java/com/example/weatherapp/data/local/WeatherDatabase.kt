package com.example.weatherapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.current_weather.type_converters.CurrentWeatherResponseTypeConverter
import com.example.weatherapp.data.model.five_days_weather_forecast.type_converter.FiveDaysWeatherForecastTypeConverter
import com.example.weatherapp.data.model.current_weather.type_converters.CloudsTypeConverter
import com.example.weatherapp.data.model.current_weather.type_converters.CoordTypeConverter
import com.example.weatherapp.data.model.current_weather.type_converters.ListOfWeatherTypeConverter
import com.example.weatherapp.data.model.current_weather.type_converters.MainTypeConverter
import com.example.weatherapp.data.model.current_weather.type_converters.SysTypeConverter
import com.example.weatherapp.data.model.current_weather.type_converters.WeatherTypeConverter
import com.example.weatherapp.data.model.current_weather.type_converters.WindTypeConverter
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.type_converter.ListOfWeatherItemTypeConverter
import com.example.weatherapp.data.model.five_days_weather_forecast.type_converter.WeatherItemTypeConverter


@Database(
    entities = [CurrentWeatherResponse::class,FiveDaysWeatherForecastResponse::class,AlarmModel::class], version = 1, exportSchema = false
)

@TypeConverters(
    CurrentWeatherResponseTypeConverter::class,
    FiveDaysWeatherForecastTypeConverter::class,
    CloudsTypeConverter::class,
    CoordTypeConverter::class,
    MainTypeConverter::class,
    SysTypeConverter::class,
    ListOfWeatherTypeConverter::class,
    ListOfWeatherItemTypeConverter::class,
    WindTypeConverter::class,
    WeatherItemTypeConverter::class,
    WeatherTypeConverter::class
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun getDao(): WeatherDao

    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null
        fun getInstance(context: Context): WeatherDatabase {
            return instance ?: synchronized(context) {
                val temp =
                    Room.databaseBuilder(context, WeatherDatabase::class.java, "Weather_Database")
                        .build()
                instance = temp
                temp

            }
        }

    }


}

