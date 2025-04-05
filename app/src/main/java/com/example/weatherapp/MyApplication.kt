package com.example.weatherapp

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.example.weatherapp.utilis.Strings

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val notificationSound =
                Uri.parse("android.resource://${this.packageName}/raw/tropical_alarm_clock")

            val attributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(
                Strings.CHANNEL_ID,
                getString(R.string.weather_news),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(notificationSound, attributes)
            }
            channel.description =
                getString(R.string.shows_weather_news_of_a_scheduled_location_s_alarm)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

