package com.example.weatherapp.utilis

import android.icu.util.Calendar
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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


fun getWeatherGradient(icon: String): Brush {
    return when (icon) {
        // ☀️ Clear Sky (Sunny)
        "01d" -> Brush.linearGradient(
            colors = listOf(Color(0xFF6EC6FF), Color(0xFF2196F3), Color(0xFF64B5F6), Color(0xFFFFD54F))
        ) // Bright Blue & Yellow
        "01n" -> Brush.linearGradient(
            colors = listOf(Color(0xFF1B2A49), Color(0xFF16213E), Color(0xFF0F3460), Color(0xFF274170))
        ) // Deep Dark Blue

        // 🌤️ Few Clouds
        "02d" -> Brush.linearGradient(
            colors = listOf(Color(0xFF90CAF9), Color(0xFF64B5F6), Color(0xFFFFD54F))
        ) // Soft Blue with Sunlight
        "02n" -> Brush.linearGradient(
            colors = listOf(Color(0xFF1F2C4A), Color(0xFF1B2A49), Color(0xFF3E5481))
        ) // Cloudy Night Blue

        // ☁️ Scattered Clouds
        "03d" -> Brush.linearGradient(
            colors = listOf(Color(0xFFB0BEC5), Color(0xFF90A4AE), Color(0xFF607D8B))
        ) // Grayish-Blue Cloudy Sky
        "03n" -> Brush.linearGradient(
            colors = listOf(Color(0xFF1B2A49), Color(0xFF263859), Color(0xFF3E5481))
        ) // Darker Cloudy Night

        // ☁️ Broken Clouds
        "04d" -> Brush.linearGradient(
            colors = listOf(Color(0xFF78909C), Color(0xFF546E7A), Color(0xFF37474F))
        ) // Heavy Grayish Clouds
        "04n" -> Brush.linearGradient(
            colors = listOf(Color(0xFF1B2A49), Color(0xFF263859), Color(0xFF3A3A3A))
        ) // Dark Broken Clouds

        // 🌧️ Shower Rain
        "09d" -> Brush.linearGradient(
            colors = listOf(Color(0xFF5E718D), Color(0xFF90A4AE), Color(0xFF607D8B))
        ) // Grayish Rainy Day
        "09n" -> Brush.linearGradient(
            colors = listOf(Color(0xFF1B2A49), Color(0xFF2B3A42), Color(0xFF3E3E3E))
        ) // Dark Rainy Night

        // 🌦️ Rain
        "10d" -> Brush.linearGradient(
            colors = listOf(Color(0xFF5A738E), Color(0xFF809FB8), Color(0xFFB3C6E7))
        ) // Light Blue Rain
        "10n" -> Brush.linearGradient(
            colors = listOf(Color(0xFF1B2A49), Color(0xFF2B3A42), Color(0xFF3E3E3E))
        ) // Midnight Rain

        // ⚡ Thunderstorm
        "11d" -> Brush.linearGradient(
            colors = listOf(Color(0xFF424242), Color(0xFF616161), Color(0xFF9E9E9E))
        ) // Stormy Gray
        "11n" -> Brush.linearGradient(
            colors = listOf(Color(0xFF000000), Color(0xFF1B1B1B), Color(0xFF3E3E3E))
        ) // Dark Storm Night

        // ❄️ Snow
        "13d" -> Brush.linearGradient(
            colors = listOf(Color(0xFFDDEEFF), Color(0xFFB3C6E7), Color(0xFFFFFFFF))
        ) // White & Ice Blue
        "13n" -> Brush.linearGradient(
            colors = listOf(Color(0xFF5C6378), Color(0xFF778899), Color(0xFFB3C6E7))
        ) // Cool Gray Night

        // 🌫️ Mist/Fog
        "50d" -> Brush.linearGradient(
            colors = listOf(Color(0xFFBDC3C7), Color(0xFFD3D3D3), Color(0xFFE0E0E0))
        ) // Light Gray Mist
        "50n" -> Brush.linearGradient(
            colors = listOf(Color(0xFF4A4A4A), Color(0xFF696969), Color(0xFF9E9E9E))
        ) // Dark Mist

        // Default Gradient
        else -> Brush.linearGradient(
            colors = listOf(Color.Gray, Color.LightGray)
        )
    }
}




//fun getTheme():Brush{
//
//    return if(isMorning()){
//        DayTheme
//    }else{
//        NightTheme
//    }
//
//}

fun formatDateTime(input: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEEE dd MMM yyy", Locale.getDefault())

    val date = inputFormat.parse(input)
    return date?.let { outputFormat.format(it) } ?: "Invalid Date"
}

fun secondFormatDateTime(input: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())

    val date = inputFormat.parse(input)
    return date?.let { outputFormat.format(it) } ?: "Invalid Date"
}

fun formatTime(input: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("ha", Locale.getDefault())

    val date = inputFormat.parse(input)
    return date?.let { outputFormat.format(it) } ?: "Invalid Date"
}

fun getCurrentDate(amount:Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, amount)
    val dateFormat = SimpleDateFormat("EEEE dd MMM yyyy", Locale.getDefault())
    return dateFormat.format(calendar.time)
}