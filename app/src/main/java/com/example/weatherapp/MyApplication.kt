package com.example.weatherapp

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.example.weatherapp.utilis.Strings

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

    }

    private fun createNotificationChannel(){
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val channel = NotificationChannel(
                Strings.CHANNEL_ID,
                getString(R.string.weather_news),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description =
                getString(R.string.shows_weather_news_of_a_scheduled_location_s_alarm)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

