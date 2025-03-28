package com.example.weatherapp.data.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.data.remote.RetrofitFactory
import com.example.weatherapp.utilis.Strings
import com.google.gson.Gson

class WeatherWorkManager(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val alarmManager: AlarmManager = applicationContext.getSystemService(AlarmManager::class.java)
        val longitude = inputData.getDouble(Strings.LONG_CONST, 0.0)
        val latitude = inputData.getDouble(Strings.LAT_CONST, 0.0)
        val requestCode = inputData.getInt(Strings.CODE_CONST, 0)
        Log.i("TAG", "doWork: $longitude , $latitude")
        val result =
            RetrofitFactory.apiService.getCurrentWeather(longitude = longitude, latitude = latitude)
        val gson = Gson()
        val stringResult = gson.toJson(result)
        val intent = Intent(applicationContext, WeatherBroadcastReceiver::class.java)
        intent.putExtra(Strings.RESULT_CONST, stringResult)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            requestCode,
            intent, PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            SystemClock.elapsedRealtime(),
            pendingIntent
        )
        return Result.success()
    }


}