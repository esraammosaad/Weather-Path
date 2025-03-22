package com.example.weatherapp.utilis

import android.icu.util.Calendar
import androidx.compose.ui.graphics.Brush
import com.example.weatherapp.ui.theme.DayTheme
import com.example.weatherapp.ui.theme.NightTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun isMorning(): Boolean {
    val calendar = Calendar.getInstance()
    val timeOfDay = calendar[Calendar.HOUR_OF_DAY]
    return timeOfDay in 0..15
}


fun convertUnixToTime(unixTime: Long): String {
    val date = Date(unixTime * 1000)
    val format = SimpleDateFormat("h:mm a", Locale.getDefault())
    return format.format(date)
}



fun getTheme():Brush{

    return if(isMorning()){
        DayTheme
    }else{
        NightTheme
    }

}

fun formatDateTime(input: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEEE dd-MMM-yyyy", Locale.getDefault())

    val date = inputFormat.parse(input)
    return date?.let { outputFormat.format(it) } ?: "Invalid Date"
}

fun formatTime(input: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("ha", Locale.getDefault())

    val date = inputFormat.parse(input)
    return date?.let { outputFormat.format(it) } ?: "Invalid Date"
}

fun getCurrentDate(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEEE dd MMM yyyy", Locale.getDefault())
    return dateFormat.format(calendar.time)
}