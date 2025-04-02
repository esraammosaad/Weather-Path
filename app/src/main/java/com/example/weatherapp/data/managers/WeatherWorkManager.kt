package com.example.weatherapp.data.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.SystemClock
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.remote.RetrofitFactory
import com.example.weatherapp.utilis.Strings
import com.google.gson.Gson
import java.util.Locale

class WeatherWorkManager(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val alarmManager: AlarmManager =
            applicationContext.getSystemService(AlarmManager::class.java)
        val longitude = inputData.getDouble(Strings.LONG_CONST, 0.0)
        val latitude = inputData.getDouble(Strings.LAT_CONST, 0.0)
        val requestCode = inputData.getInt(Strings.CODE_CONST, 0)
        Log.i("TAG", "doWork: $requestCode")
        Log.i("TAG", "doWork: $longitude , $latitude")
        val result =
            RetrofitFactory.apiService.getCurrentWeather(
                longitude = longitude,
                latitude = latitude,
                language = LocalStorageDataSource.getInstance(applicationContext).getLanguageCode,
                unit = LocalStorageDataSource.getInstance(applicationContext).getTempUnit
            )
        Log.i("TAG", "doWork:  result :  $result")

        val list = Geocoder(
            applicationContext,
            Locale(LocalStorageDataSource.getInstance(applicationContext).getLanguageCode)
        ).getFromLocation(latitude, longitude, 1)
        if (!list.isNullOrEmpty()) {
            result.countryName = list[0].countryName?:result.sys.country
            result.cityName = list[0].locality?:result.name
        }
        WeatherDatabase.getInstance(applicationContext).getDao().selectAllAlarms().collect {
            val gson = Gson()
            val stringResult = gson.toJson(result)
            val intent = Intent(applicationContext, WeatherBroadcastReceiver::class.java)
            intent.putExtra(Strings.RESULT_CONST, stringResult)
            Log.i("TAG", "doWork: string result :  $stringResult")
            intent.putExtra(
                Strings.ALARM_TYPE,
                it.first { item -> item.locationId == result.id }.alarmType
            )
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

            )
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                SystemClock.elapsedRealtime()+20000,
                pendingIntent
            )

            WeatherDatabase.getInstance(applicationContext).getDao().deleteAlarm(result.id)
        }
        return Result.success()
    }


}