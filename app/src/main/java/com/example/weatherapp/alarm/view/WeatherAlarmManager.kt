package com.example.weatherapp.alarm.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.example.weatherapp.utilis.AlarmReceiver

class WeatherAlarmManager(val context: Context) {
    private val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)
    fun schedule(message: String, requestCode : Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("msg", message)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent, PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            SystemClock.elapsedRealtime() + 100000,
            pendingIntent
        )
    }

    fun cancel() {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            intent, PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

}