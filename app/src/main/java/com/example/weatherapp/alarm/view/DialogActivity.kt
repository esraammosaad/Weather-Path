package com.example.weatherapp.alarm.view

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.WeatherStatusImageDisplay
import com.google.gson.Gson

class DialogActivity : ComponentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        window.setBackgroundDrawable(ColorDrawable(0))
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val result = intent.getStringExtra(Strings.RESULT_CONST)
        val gson = Gson()
        val response = gson.fromJson(result, CurrentWeatherResponse::class.java)
        val mediaPlayer = MediaPlayer.create(this@DialogActivity, R.raw.sound_track_two)
        mediaPlayer.start()
        setContent {
            AlertScreen(response, onConfirmClicked = {
                finish()
            }, onDismissClicked = { finish() })

            notificationManager.notify(1, createNotification(this))



        }
    }

    override fun onStart() {
        super.onStart()
        askForNotificationPermission()
    }

    private fun askForNotificationPermission() {
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            if (!notificationManager.areNotificationsEnabled()) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    10
                )
            } else {
                createNotification(this)
            }
        }
    }

    private fun createNotification(context: Context): Notification {
        val channelId = Strings.CHANNEL_ID
        val channelName = "Full Screen Alert"

        // Create Notification Channel for Android 8+
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows notifications as alerts on the screen"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC // Show on lock screen
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Full-screen intent (forces pop-up)
        val fullScreenIntent = Intent(context, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the Notification
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.sun)
            .setContentTitle("Weather Alert")
            .setContentText("Check today's weather!")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Use PRIORITY_HIGH for heads-up
            .setDefaults(Notification.DEFAULT_ALL) // Enables sound, vibration, and LED
            .setCategory(NotificationCompat.CATEGORY_ALARM) // Makes it more urgent
            .setFullScreenIntent(fullScreenPendingIntent, true) // Forces heads-up display
            .setAutoCancel(true)
            .build()
    }


//    private fun createNotification(context: Context): Notification {
//        if (VERSION.SDK_INT >= VERSION_CODES.O) {
//            val name = "channel name"
//            val desc = "channel desc"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel(Strings.CHANNEL_ID, name, importance)
//            channel.description = desc
//            val notificationManager =
//                context.getSystemService(NotificationManager::class.java) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//          channel.lockscreenVisibility= Notification.VISIBILITY_PUBLIC
//
//
//
//        }
//        val fullScreenIntent = Intent(this, MainActivity::class.java)
//        val fullScreenPendingIntent = PendingIntent.getActivity(
//            this, 0,
//            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
//        )
//
//        val builder = NotificationCompat.Builder(context, Strings.CHANNEL_ID)
//            .setSmallIcon(R.drawable.sun)
//            .setContentTitle("Downloading")
//            .setContentText("Image Downloading....")
//            .setPriority(NotificationManager.IMPORTANCE_HIGH)
//            .setCategory(NotificationCompat.CATEGORY_CALL)
//            .setFullScreenIntent(fullScreenPendingIntent, true)
//            .setAutoCancel(true)
//            .build()
//        return builder
//
//    }

}

@Composable
private fun AlertScreen(
    currentWeatherResponse: CurrentWeatherResponse,
    onConfirmClicked: () -> Unit,
    onDismissClicked: () -> Unit
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
        Box(
            modifier = Modifier
                .background(
                    brush = getWeatherGradient(
                        currentWeatherResponse.weather.firstOrNull()?.icon ?: ""
                    ), shape = RoundedCornerShape(15.dp)
                )
                .padding(24.dp)
                .height(210.dp)
        ) {
            Column(Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "${currentWeatherResponse.name}'s Weather",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Color.White,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = currentWeatherResponse.weather.firstOrNull()?.description
                            ?: "",
                        fontWeight = FontWeight.Medium,
                        fontSize = 24.sp,
                        fontFamily = poppinsFontFamily,
                        color = OffWhite
                    )
                    WeatherStatusImageDisplay(
                        currentWeatherResponse.weather[0].icon
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        "Today's weather in ${currentWeatherResponse.name} is ${currentWeatherResponse.weather.firstOrNull()?.description} with a temperature of ${currentWeatherResponse.main.temp}°C. Stay prepared and enjoy your day!",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(
                        onClick = {
                            onDismissClicked.invoke()
                        }, colors = ButtonColors(
                            containerColor = PrimaryColor,
                            contentColor = Color.White,
                            disabledContainerColor = PrimaryColor,
                            disabledContentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            stringResource(R.string.dismiss),
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            onConfirmClicked.invoke()
                        },
                        colors = ButtonColors(
                            containerColor = OffWhite,
                            contentColor = Color.Black,
                            disabledContainerColor = OffWhite,
                            disabledContentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, getWeatherGradient())
                    ) {
                        Text(
                            stringResource(R.string.confirm),
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                        )
                    }
                }

            }
        }
    }
}