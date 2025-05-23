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
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.remote.RetrofitFactory
import com.example.weatherapp.utilis.Strings
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
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
        val result: CurrentWeatherResponse = try {
            Log.i("TAG", "doWork: nettttttttt")
            RetrofitFactory.apiService.getCurrentWeather(
                longitude = longitude,
                latitude = latitude,
                language = LocalStorageDataSource.getInstance(applicationContext).getLanguageCode.substring(0,2),
                unit = LocalStorageDataSource.getInstance(applicationContext).getTempUnit
            )
        } catch (e: Exception) {
            Log.i("TAG", "doWork:no nettttttttt ${e.message.toString()}")
            WeatherDatabase.getInstance(applicationContext).getDao().selectDayWeather(
                longitude = longitude,
                latitude = latitude
            ).first()
        }
        Log.i("TAG", "doWork:  result :  $result")
        try {
            val list = Geocoder(
                applicationContext,
                Locale(LocalStorageDataSource.getInstance(applicationContext).getLanguageCode.substring(0,2))
            ).getFromLocation(latitude, longitude, 1)
            if (!list.isNullOrEmpty()) {
                result.countryName = list[0].countryName ?: result.sys.country
                result.cityName = list[0].locality ?: result.name
            }
        } catch (e: Exception) {
            result.countryName = result.sys.country
            result.cityName = result.name
        }
        result.latitude = latitude
        result.longitude = longitude
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
                SystemClock.elapsedRealtime() + 20000,
                pendingIntent
            )
            WeatherDatabase.getInstance(applicationContext).getDao().deleteAlarm(result.id)
            Log.i("TAG", "doWork: deleted")
        }
        return Result.success()
    }
}