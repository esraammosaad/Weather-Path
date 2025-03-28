package com.example.weatherapp.alarm.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.weatherapp.ConfirmationDialog
import com.example.weatherapp.R
import com.example.weatherapp.data.managers.CHANNEL_ID
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.utilis.Strings
import com.google.gson.Gson


class DialogActivity : ComponentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(0))
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val result = intent.getStringExtra(Strings.RESULT_CONST)
        val gson = Gson()
        val response = gson.fromJson(result , CurrentWeatherResponse::class.java)
        setContent {
            ConfirmationDialog(
                onConfirmation = {
                    finishAffinity()
                },
                onDismiss = { finishAffinity() },
                dialogTitle = response.name,
                dialogText = response.sys.country,
                showRadioButton = false,
                onOptionClicked = {}
            )

        }


    }

    override fun onStart() {
        super.onStart()
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            if (!notificationManager.areNotificationsEnabled()) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    10
                )
            } else {
                createNotification(this)
            }


        }
    }

    private fun createNotification(context: Context): NotificationCompat.Builder {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val name = "channel name"
            val desc = "channel desc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = desc
            val notificationManager =
                context.getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.sun)
            .setContentTitle("Downloading")
            .setContentText("Image Downloading....")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setProgress(100, 0, true)
            .setAutoCancel(true)
        return builder

    }

}