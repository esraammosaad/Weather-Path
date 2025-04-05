package com.example.weatherapp.data.managers

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.utilis.Strings
import java.util.concurrent.TimeUnit

object WorkManagerHelper {

    fun requestWorkManagerForSet(
        selectedWeather: CurrentWeatherResponse?,
        context: Context,
        duration: Long
    ) {
        val inputData = Data.Builder()
        inputData.putDouble(Strings.LONG_CONST, selectedWeather?.longitude ?: 0.0)
        inputData.putDouble(Strings.LAT_CONST, selectedWeather?.latitude ?: 0.0)
        inputData.putInt(Strings.CODE_CONST, selectedWeather?.id ?: 0)
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<WeatherWorkManager>()
                .setInputData(inputData.build()).setInitialDelay(duration, TimeUnit.MILLISECONDS)
                .addTag(selectedWeather?.id.toString())
                .build()
        WorkManager.getInstance(context).enqueue(workRequest)

    }

    fun requestWorkManagerForUpdate(
        selectedAlarm: AlarmModel?,
        context: Context,
        duration: Long
    ) {
        WorkManager.getInstance(context).cancelAllWorkByTag(selectedAlarm?.locationId.toString())
        val inputData = Data.Builder()
        inputData.putDouble(Strings.LONG_CONST, selectedAlarm?.longitude ?: 0.0)
        inputData.putDouble(Strings.LAT_CONST, selectedAlarm?.latitude ?: 0.0)
        inputData.putInt(Strings.CODE_CONST, selectedAlarm?.locationId ?: 0)
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<WeatherWorkManager>()
                .setInputData(inputData.build()).setInitialDelay(duration, TimeUnit.MILLISECONDS)
                .addTag(selectedAlarm?.locationId.toString())
                .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }


}
