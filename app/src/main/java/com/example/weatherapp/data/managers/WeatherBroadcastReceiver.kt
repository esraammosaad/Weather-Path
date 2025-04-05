package com.example.weatherapp.data.managers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.provider.Settings
import android.util.Log
import com.example.weatherapp.favorite_alarm_features.alarm.view.DialogActivity
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.notification.NotificationHelper
import com.google.gson.Gson


class WeatherBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val response = intent.getStringExtra(Strings.RESULT_CONST)
        val alarmType = intent.getStringExtra(Strings.ALARM_TYPE)
        val gson = Gson()
        val currentWeatherResponse = gson.fromJson(response, CurrentWeatherResponse::class.java)
        Log.i("TAG", "onReceive: $response")
        if (alarmType == "Alert") {
            if (Settings.canDrawOverlays(context)) {
                val dialogIntent = Intent(context, DialogActivity::class.java).addFlags(
                    FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_REORDER_TO_FRONT
                ).putExtra(Strings.RESULT_CONST, response)
                context.startActivity(dialogIntent)
            }
        }else{
            notificationManager.notify(1, NotificationHelper.createNotification(context,true,currentWeatherResponse))
        }


    }
}





