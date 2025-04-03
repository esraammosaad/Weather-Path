package com.example.weatherapp.utilis.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.utilis.localization.LocalizationHelper
import com.example.weatherapp.utilis.Strings

class NotificationHelper {
    companion object {
        fun createNotification(
            context: Context,
            autoCancel: Boolean = true,
            currentWeatherResponse: CurrentWeatherResponse
        ): Notification {
            val fullScreenIntent = Intent(context, MainActivity::class.java)
            val fullScreenPendingIntent = PendingIntent.getActivity(
                context, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val intent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
                putExtra("action",0)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val intent2 = Intent(context, NotificationBroadcastReceiver::class.java).apply {
                putExtra("action",1)
            }
            val pendingIntent2 = PendingIntent.getBroadcast(
                context,
                0,
                intent2,
                PendingIntent.FLAG_IMMUTABLE
            )
            val localContext = LocalizationHelper.getLocalizedContext(context,LocalStorageDataSource.getInstance(context).getLanguageCode)
            return NotificationCompat.Builder(context, Strings.CHANNEL_ID)
                .setSmallIcon(R.drawable.sun)
                .setContentTitle(
                    localContext.getString(
                        R.string.s_weather,
                        currentWeatherResponse.cityName
                    )
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            localContext.getString(
                                R.string.today_s_weather_in_is_with_a_temperature_of,
                                currentWeatherResponse.cityName,
                                currentWeatherResponse.weather.firstOrNull()?.description ?: "",
                                currentWeatherResponse.main.temp.toString()
                            )
                                    + localContext.getString(LocalStorageDataSource.getInstance(context).getTempSymbol)
                                    + " "
                                    + localContext.getString(
                                R.string.stay_prepared_and_enjoy_your_day
                            )
                        )
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setAutoCancel(autoCancel)
                .addAction(0, localContext.getString(R.string.confirm), pendingIntent)
                .addAction(0, context.getString(R.string.snooze), pendingIntent2)
                .build()
        }
    }
}