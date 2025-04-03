package com.example.weatherapp.utilis.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.weatherapp.alarm.view.snoozeAlarm
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSourceImpl
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.remote.RetrofitFactory
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.google.gson.Gson

class NotificationBroadcastReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent?) {
        val result = intent?.getIntExtra("action", 0)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (result == 0) {
            Log.i("TAG", "onReceive: $result -------------")
            notificationManager.cancel(1)
        } else {
            Log.i("TAG", "onReceive: $result -------------")
            val response = intent?.getStringExtra("response")
            val gson = Gson()
            val responseObject = gson.fromJson(response, CurrentWeatherResponse::class.java)

            val favoriteViewModelImpl = FavoriteViewModelImpl(
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