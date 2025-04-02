package com.example.weatherapp.utilis

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.weatherapp.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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



fun getWeatherGradient(icon: String = "" ): Brush {
    return when (icon) {
        // ☀️ Clear Sky (Sunny)
        "01d" -> Brush.linearGradient(
            listOf(Color(0xFF1B2A49), Color(0xFF6A85B6), Color(0xFFBAC8E0))
        ) // Soft navy meets crisp sky blue
        "01n" -> Brush.linearGradient(
            listOf(Color(0xFF0D1B2A), Color(0xFF16213E), Color(0xFF1B2A49))
        ) // Deep navy to midnight blue

        // 🌤️ Few Clouds
        "02d" -> Brush.linearGradient(
            listOf(Color(0xFF1B2A49), Color(0xFF8FA6C1), Color(0xFFBAC8E0))
        ) // Muted blue with soft clouds
        "02n" -> Brush.linearGradient(
            listOf(Color(0xFF16213E), Color(0xFF1B2A49), Color(0xFF3E5481))
        ) // Navy with a hint of cool gray

        // ☁️ Scattered Clouds
        "03d" -> Brush.linearGradient(
            listOf(Color(0xFF607D8B), Color(0xFF90A4AE), Color(0xFFB0BEC5))
        ) // Soft gray-blue tones
        "03n" -> Brush.linearGradient(
            listOf(Color(0xFF1B2A49), Color(0xFF263859), Color(0xFF3E5481))
        ) // Deeper navy with cool undertones

        // ☁️ Broken Clouds
        "04d" -> Brush.linearGradient(
            listOf(Color(0xFF37474F), Color(0xFF546E7A), Color(0xFF78909C))
        ) // Heavy gray with a navy touch
        "04n" -> Brush.linearGradient(
            listOf(Color(0xFF1B2A49), Color(0xFF263859), Color(0xFF3A3A3A))
        ) // Stormy navy blues

        // 🌧️ Shower Rain
        "09d" -> Brush.linearGradient(
            listOf(Color(0xFF5E718D), Color(0xFF90A4AE), Color(0xFF607D8B))
        ) // Muted rainy sky blue
        "09n" -> Brush.linearGradient(
            listOf(Color(0xFF1B2A49), Color(0xFF2B3A42), Color(0xFF3E3E3E))
        ) // Dark rainstorm vibes

        // 🌦️ Rain
        "10d" -> Brush.linearGradient(
            listOf(Color(0xFF5A738E), Color(0xFF809FB8), Color(0xFFB3C6E7))
        ) // Soft rain with navy blue contrast
        "10n" -> Brush.linearGradient(
            listOf(Color(0xFF1B2A49), Color(0xFF2B3A42), Color(0xFF3E3E3E))
        ) // Midnight rain tones

        // ⛈️ Thunderstorm
        "11d" -> Brush.linearGradient(
            listOf(Color(0xFF424242), Color(0xFF616161), Color(0xFF9E9E9E))
        ) // Stormy gray with depth
        "11n" -> Brush.linearGradient(
            listOf(Color(0xFF000000), Color(0xFF1B1B1B), Color(0xFF3E3E3E))
        ) // Dark stormy navy

        // ❄️ Snow
        "13d" -> Brush.linearGradient(
            listOf(Color(0xFF5C6378), Color(0xFF778899), Color(0xFFB3C6E7))
        ) // Icy cool blues
        "13n" -> Brush.linearGradient(
            listOf(Color(0xFF5C6378), Color(0xFF778899), Color(0xFFB3C6E7))
        ) // Winter navy tones

        // 🌫️ Mist/Fog
        "50d" -> Brush.linearGradient(
            listOf(Color(0xFF4A4A4A), Color(0xFF696969), Color(0xFF9E9E9E))
        ) // Soft foggy gray
        "50n" -> Brush.linearGradient(
            listOf(Color(0xFF4A4A4A), Color(0xFF696969), Color(0xFF9E9E9E))
        ) // Foggy navy hues

        // Default Gradient
        else -> Brush.linearGradient(
//            listOf(Color(0xFF1B2A49), Color(0xFF6A85B6))
            listOf(Color(0xFF6A85B6), Color(0xFFBAC8E0))

        ) // Default navy gradient
    }
}

fun getWeatherImage(icon: String = "" ): Int {
    return when (icon) {
        // ☀️ Clear Sky (Sunny)
        "01d" -> R.drawable.sunny// Bright Blue & Yellow
        "01n" -> R.drawable.moon// Deep Dark Blue

        // 🌤️ Few Clouds
        "02d" ->R.drawable.partlycloudy // Soft Blue with Sunlight
        "02n" -> R.drawable.cloudy_night // Cloudy Night Blue

        // ☁️ Scattered Clouds
        "03d" -> R.drawable.scatterd// Grayish-Blue Cloudy Sky
        "03n" -> R.drawable.scatterd// Darker Cloudy Night

        // ☁️ Broken Clouds
        "04d" -> R.drawable.scatterd// Heavy Grayish Clouds
        "04n" ->R.drawable.scatterd // Dark Broken Clouds

        // 🌧️ Shower Rain
        "09d" -> R.drawable.rainy_bold// Grayish Rainy Day
        "09n" -> R.drawable.rainy_bold // Dark Rainy Night

        // 🌦️ Rain
        "10d" -> R.drawable.rainy_bold // Light Blue Rain
        "10n" -> R.drawable.rainy_bold // Midnight Rain

        // ⚡ Thunderstorm
        "11d" -> R.drawable.rainthunder// Stormy Gray
        "11n" -> R.drawable.rainthunder_bold // Dark Storm Night

        // ❄️ Snow
        "13d" -> R.drawable.snow // White & Ice Blue
        "13n" -> R.drawable.snow // Cool Gray Night

        // 🌫️ Mist/Fog
        "50d" -> R.drawable.scatterd// Light Gray Mist
        "50n" -> R.drawable.scatterd// Dark Mist

        // Default Gradient
        else -> R.drawable.cloud
    }
}

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

@RequiresApi(Build.VERSION_CODES.O)
fun getTimeFromTimestamp(timestamp: Int, offsetInSeconds: Int): String {
    val zoneId = ZoneId.ofOffset("UTC", java.time.ZoneOffset.ofTotalSeconds(offsetInSeconds))
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp.toLong()), zoneId)
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return dateTime.format(formatter)
}

fun calculateDelay(targetYear: Int, targetMonth: Int, targetDay: Int, targetHour: Int, targetMinute: Int): Long {
    val now = java.util.Calendar.getInstance()

    val targetTime = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.YEAR, targetYear)
        set(java.util.Calendar.MONTH, targetMonth - 1)
        set(java.util.Calendar.DAY_OF_MONTH, targetDay)
        set(java.util.Calendar.HOUR_OF_DAY, targetHour)
        set(java.util.Calendar.MINUTE, targetMinute)
        set(java.util.Calendar.SECOND, 0)
    }

    val delay = targetTime.timeInMillis - now.timeInMillis

    return if (delay > 0) delay else 0L
}
