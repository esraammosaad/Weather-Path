package com.example.weatherapp.utilis

import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


fun convertUnixToTime(unixTime: Long): String {
    val date = Date(unixTime * 1000)
    val format = SimpleDateFormat("h:mm a", Locale.getDefault())
    return format.format(date)
}

fun getWeatherGradient(icon: String = ""): Brush {
    return when (icon) {
        "01d" -> Brush.linearGradient(
            listOf(Color(0xFF1B2A49), Color(0xFF6A85B6), Color(0xFFBAC8E0))
        )

        "01n" -> Brush.linearGradient(
            listOf(Color(0xFF0D1B2A), Color(0xFF16213E), Color(0xFF1B2A49))
        )

        "02d" -> Brush.linearGradient(
            listOf(Color(0xFF1B2A49), Color(0xFF8FA6C1), Color(0xFFBAC8E0))
        )

        "02n" -> Brush.linearGradient(
            listOf(Color(0xFF16213E), Color(0xFF1B2A49), Color(0xFF3E5481))
        )

        "03d" -> Brush.linearGradient(
            listOf(Color(0xFF607D8B), Color(0xFF90A4AE), Color(0xFFB0BEC5))
        )

        "03n" -> Brush.linearGradient(
            listOf(Color(0xFF1B2A49), Color(0xFF263859), Color(0xFF3E5481))
        )

        "04d" -> Brush.linearGradient(
            listOf(Color(0xFF37474F), Color(0xFF546E7A), Color(0xFF78909C))
        )

        "04n" -> Brush.linearGradient(
            listOf(Color(0xFF1B2A49), Color(0xFF263859), Color(0xFF3A3A3A))
        )

        "09d" -> Brush.linearGradient(
            listOf(Color(0xFF5E718D), Color(0xFF90A4AE), Color(0xFF607D8B))
        )

        "09n" -> Brush.linearGradient(
            listOf(Color(0xFF1B2A49), Color(0xFF2B3A42), Color(0xFF3E3E3E))
        )

        "10d" -> Brush.linearGradient(
            listOf(Color(0xFF5A738E), Color(0xFF809FB8), Color(0xFFB3C6E7))
        )

        "10n" -> Brush.linearGradient(
            listOf(Color(0xFF1B2A49), Color(0xFF2B3A42), Color(0xFF3E3E3E))
        )

        // ⛈️ Thunderstorm
        "11d" -> Brush.linearGradient(
            listOf(Color(0xFF424242), Color(0xFF616161), Color(0xFF9E9E9E))
        )

        "11n" -> Brush.linearGradient(
            listOf(Color(0xFF000000), Color(0xFF1B1B1B), Color(0xFF3E3E3E))
        )

        "13d" -> Brush.linearGradient(
            listOf(Color(0xFF5C6378), Color(0xFF778899), Color(0xFFB3C6E7))
        )

        "13n" -> Brush.linearGradient(
            listOf(Color(0xFF5C6378), Color(0xFF778899), Color(0xFFB3C6E7))
        ) // Winter navy tones

        "50d" -> Brush.linearGradient(
            listOf(Color(0xFF4A4A4A), Color(0xFF696969), Color(0xFF9E9E9E))
        )

        "50n" -> Brush.linearGradient(
            listOf(Color(0xFF4A4A4A), Color(0xFF696969), Color(0xFF9E9E9E))
        )

        else -> Brush.linearGradient(
//            listOf(Color(0xFF1B2A49), Color(0xFF6A85B6))
            listOf(Color(0xFF6A85B6), Color(0xFFBAC8E0))

        )
    }
}

fun getWeatherImage(icon: String = ""): Int {
    return when (icon) {
        // ☀️ Clear Sky (Sunny)
        "01d" -> R.drawable.sunny// Bright Blue & Yellow
        "01n" -> R.drawable.moon// Deep Dark Blue

        // 🌤️ Few Clouds
        "02d" -> R.drawable.partlycloudy // Soft Blue with Sunlight
        "02n" -> R.drawable.cloudy_night // Cloudy Night Blue

        // ☁️ Scattered Clouds
        "03d" -> R.drawable.scatterd// Grayish-Blue Cloudy Sky
        "03n" -> R.drawable.scatterd// Darker Cloudy Night

        // ☁️ Broken Clouds
        "04d" -> R.drawable.scatterd// Heavy Grayish Clouds
        "04n" -> R.drawable.scatterd // Dark Broken Clouds

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

fun getWeatherBackground(icon: String = ""): Int {
    return when (icon) {
        // ☀️ Clear Sky (Sunny)
        "01d" -> R.raw.stars// Bright Blue & Yellow
        "01n" -> R.raw.stars// Deep Dark Blue

        // 🌤️ Few Clouds
        "02d" -> R.raw.clouds // Soft Blue with Sunlight
        "02n" -> R.raw.clouds // Cloudy Night Blue

        // ☁️ Scattered Clouds
        "03d" -> R.raw.clouds// Grayish-Blue Cloudy Sky
        "03n" -> R.raw.clouds// Darker Cloudy Night

        // ☁️ Broken Clouds
        "04d" -> R.raw.clouds// Heavy Grayish Clouds
        "04n" -> R.raw.clouds // Dark Broken Clouds

        // 🌧️ Shower Rain
        "09d" -> R.raw.rain// Grayish Rainy Day
        "09n" -> R.raw.rain // Dark Rainy Night

        // 🌦️ Rain
        "10d" -> R.raw.rain // Light Blue Rain
        "10n" -> R.raw.rain // Midnight Rain

        // ⚡ Thunderstorm
        "11d" -> R.raw.clouds// Stormy Gray
        "11n" -> R.raw.clouds // Dark Storm Night

        // ❄️ Snow
        "13d" -> R.raw.stars // White & Ice Blue
        "13n" -> R.raw.stars // Cool Gray Night

        // 🌫️ Mist/Fog
        "50d" -> R.raw.clouds// Light Gray Mist
        "50n" -> R.raw.clouds// Dark Mist

        // Default Gradient
        else -> R.raw.stars
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

fun getCurrentDate(amount: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, amount)
    val dateFormat = SimpleDateFormat("EEEE dd MMM yyyy", Locale.getDefault())
    return dateFormat.format(calendar.time)
}


@RequiresApi(Build.VERSION_CODES.O)
fun getTimeFromTimestamp(timestamp: Int, offsetInSeconds: Int,context: Context): String {
    val zoneId = ZoneId.ofOffset("UTC", java.time.ZoneOffset.ofTotalSeconds(offsetInSeconds))
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp.toLong()), zoneId)
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale(LocalStorageDataSource.getInstance(context = context).getLanguageCode))

    return dateTime.format(formatter)
}



