package com.example.weatherapp.utilis.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weatherapp.favorite_alarm_features.alarm.view.snoozeAlarm
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSourceImpl
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.remote.RetrofitFactory
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteAndAlarmSharedViewModelImpl
import com.example.weatherapp.utilis.Strings
import com.google.gson.Gson

class NotificationBroadcastReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent?) {
        val result = intent?.getIntExtra(Strings.NOTIFICATION_ACTION, 0)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (result == 0) {
            notificationManager.cancel(1)
        } else {
            val response = intent?.getStringExtra(Strings.NOTIFICATION_RESPONSE)
            val gson = Gson()
            val responseObject = gson.fromJson(response, CurrentWeatherResponse::class.java)

            val favoriteViewModelImpl = FavoriteAndAlarmSharedViewModelImpl(
                WeatherRepositoryImpl.getInstance(
                    weatherRemoteDataSourceImpl = WeatherRemoteDataSourceImpl(RetrofitFactory.apiService),
                    weatherLocalDataSourceImpl = WeatherLocalDataSourceImpl(
                        WeatherDatabase.getInstance(
                            context
                        ).getDao()
                    )
                )
            )

            snoozeAlarm(
                response = responseObject,
                favoriteViewModelImpl = favoriteViewModelImpl,
                context = context,
                "Notification"
            )
            notificationManager.cancel(1)
        }
    }
}