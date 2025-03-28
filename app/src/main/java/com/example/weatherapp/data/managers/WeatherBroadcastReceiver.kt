package com.example.weatherapp.data.managers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.net.Uri
import android.provider.Settings
import android.util.Log
import com.example.weatherapp.alarm.view.DialogActivity
import com.example.weatherapp.utilis.Strings


const val CHANNEL_ID = "CHANNEL ID"

class WeatherBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val response = intent.getStringExtra(Strings.RESULT_CONST)
        Log.i("TAG", "onReceive: $response")
        if (!Settings.canDrawOverlays(context)) {
            val permissionIntent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:com.example.weatherapp")
            ).addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(permissionIntent)
        } else {
            val dialogIntent = Intent(context, DialogActivity::class.java).addFlags(
                FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_REORDER_TO_FRONT
            ).putExtra(Strings.RESULT_CONST,response)
            context.startActivity(dialogIntent)

        }

    }

}





