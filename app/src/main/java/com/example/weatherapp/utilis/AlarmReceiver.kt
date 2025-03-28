package com.example.weatherapp.utilis

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.weatherapp.R
import com.example.weatherapp.alarm.DialogActivity


const val CHANNEL_ID = "CHANNEL ID"

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("TAG", "onReceive: alarm received ${intent.getStringExtra("msg")}")
        if (!Settings.canDrawOverlays(context)) {
            val intent3 = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:com.example.weatherapp")
            ).addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent3)
        } else {

            val intent2 = Intent(context, DialogActivity::class.java).addFlags(
                FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP
            ).addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
            context.startActivity(intent2)

        }
    }


}
