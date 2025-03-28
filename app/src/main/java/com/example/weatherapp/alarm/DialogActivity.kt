package com.example.weatherapp.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.weatherapp.AlertDialogExample
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.utilis.CHANNEL_ID


class DialogActivity : ComponentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(0))
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )


//        val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
//        alertDialog.setTitle(" ")
//        alertDialog.setMessage("")
//        alertDialog.setIcon(R.drawable.ic_dialog_alert)
//        alertDialog.setButton("Accept"
//        ) { dialog, which ->
//            finish()
//         }
//        alertDialog.setButton2("Deny"
//        ) { dialog, which -> finish() }
//        alertDialog.show()
        setContent {
            AlertDialogExample(
                onConfirmation = {
                    finishAffinity()

                },
                onDismiss = { finishAffinity() },
                dialogTitle = "hello",
                dialogText = "everything is okay",
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