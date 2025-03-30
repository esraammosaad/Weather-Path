package com.example.weatherapp.data.managers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.alarm.view.DialogActivity
import com.example.weatherapp.utilis.Strings


class WeatherBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val response = intent.getStringExtra(Strings.RESULT_CONST)
        val alarmType = intent.getStringExtra(Strings.ALARM_TYPE)
        Log.i("TAG", "onReceive: $response")
        if (alarmType == context.getString(R.string.alert)) {
            if (Settings.canDrawOverlays(context)) {
                val dialogIntent = Intent(context, DialogActivity::class.java).addFlags(
                    FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_REORDER_TO_FRONT
                ).putExtra(Strings.RESULT_CONST, response)
                context.startActivity(dialogIntent)
            }
        }else{

            notificationManager.notify(1, createNotification(context))


        }


    }

    private fun createNotification(context: Context): Notification {
        val fullScreenIntent = Intent(context, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, Strings.CHANNEL_ID)
            .setSmallIcon(R.drawable.sun)
            .setContentTitle("Weather Alert")
            .setContentText("Check today's weather!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .build()
    }

}





