package com.example.weatherapp.utilis.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.utilis.localization.LocalizationHelper
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.convertToArabicNumbers
import com.example.weatherapp.utilis.getTimeFromTimestamp
import com.google.gson.Gson

class NotificationHelper {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
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
                putExtra(Strings.NOTIFICATION_ACTION, 0)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val intent2 = Intent(context, NotificationBroadcastReceiver::class.java).apply {
                putExtra(Strings.NOTIFICATION_ACTION, 1)
                val gson = Gson()
                val stringResponse = gson.toJson(currentWeatherResponse)
                putExtra(Strings.NOTIFICATION_RESPONSE, stringResponse)
            }
            val pendingIntent2 = PendingIntent.getBroadcast(
                context,
                currentWeatherResponse.id,
                intent2,
                PendingIntent.FLAG_IMMUTABLE
            )
            val localContext = LocalizationHelper.getLocalizedContext(
                context,
                LocalStorageDataSource.getInstance(context).getLanguageCode
            )
            return NotificationCompat.Builder(context, Strings.CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
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
                                convertToArabicNumbers(
                                    currentWeatherResponse.main.temp.toString(),
                                    context
                                )
                            )
                                    + localContext.getString(
                                LocalStorageDataSource.getInstance(
                                    context
                                ).getTempSymbol
                            )
                                    + " "
                                    + localContext.getString(
                                R.string.stay_prepared_and_enjoy_your_day
                            ) + "\n\n" + localContext.getString(R.string.last_updated) + convertToArabicNumbers(
                                getTimeFromTimestamp(
                                    offsetInSeconds = currentWeatherResponse.timezone,
                                    timestamp = currentWeatherResponse.dt, context = context

                                ), context
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