package com.example.weatherapp.data.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.example.weatherapp.utilis.Strings

class WeatherAlarmManager(val context: Context) {
    private val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)
    fun schedule(longitude: Double, latitude: Double, requestCode: Int) {
        val intent = Intent(context, WeatherBroadcastReceiver::class.java)
        intent.putExtra(Strings.LONG_CONST, longitude)
        intent.putExtra(Strings.LAT_CONST, latitude)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent, PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            SystemClock.elapsedRealtime() + 200000,
            pendingIntent
        )
    }
    fun cancel() {
        val intent = Intent(context, WeatherBroadcastReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            intent, PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

}