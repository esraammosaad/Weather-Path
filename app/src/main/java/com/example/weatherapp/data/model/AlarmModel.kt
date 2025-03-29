package com.example.weatherapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "alarm_weather_table")
data class AlarmModel(
    @PrimaryKey
    var locationId: Int,
    var date: String,
    var time: String,
    var countryName: String,
    var cityName: String,
    var status: Boolean
)