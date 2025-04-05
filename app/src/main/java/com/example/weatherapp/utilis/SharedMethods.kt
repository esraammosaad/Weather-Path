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
        )

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
        "01d" -> R.drawable.sunny
        "01n" -> R.drawable.moon

        "02d" -> R.drawable.partlycloudy
        "02n" -> R.drawable.cloudy_night

        "03d" -> R.drawable.scatterd
        "03n" -> R.drawable.scatterd

        "04d" -> R.drawable.scatterd
        "04n" -> R.drawable.scatterd

        "09d" -> R.drawable.rainy_bold
        "09n" -> R.drawable.rainy_bold

        "10d" -> R.drawable.rainy_bold
        "10n" -> R.drawable.rainy_bold

        "11d" -> R.drawable.rainthunder
        "11n" -> R.drawable.rainthunder_bold

        "13d" -> R.drawable.snow
        "13n" -> R.drawable.snow

        "50d" -> R.drawable.scatterd
        "50n" -> R.drawable.scatterd

        else -> R.drawable.cloud
    }
}

fun getWeatherBackground(icon: String = ""): Int {
    return when (icon) {
        "01d" -> R.raw.stars
        "01n" -> R.raw.stars

        "02d" -> R.raw.stars
        "02n" -> R.raw.stars

        "03d" -> R.raw.stars
        "03n" -> R.raw.stars

        "04d" -> R.raw.clouds
        "04n" -> R.raw.clouds

        "09d" -> R.raw.rain
        "09n" -> R.raw.rain

        "10d" -> R.raw.rain
        "10n" -> R.raw.rain

        "11d" -> R.raw.clouds
        "11n" -> R.raw.clouds

        "13d" -> R.raw.snow
        "13n" -> R.raw.snow

        "50d" -> R.raw.clouds
        "50n" -> R.raw.clouds

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
fun getTimeFromTimestamp(timestamp: Int, offsetInSeconds: Int, context: Context): String {
    val zoneId = ZoneId.ofOffset("UTC", java.time.ZoneOffset.ofTotalSeconds(offsetInSeconds))
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp.toLong()), zoneId)
    val formatter = DateTimeFormatter.ofPattern(
        "hh:mm a",
        Locale(LocalStorageDataSource.getInstance(context = context).getLanguageCode)
    )

    return dateTime.format(formatter)
}

fun convertToArabicNumbers(input: String, context: Context): String {
    val arabicDigits = mapOf(
        '0' to '٠',
        '1' to '١',
        '2' to '٢',
        '3' to '٣',
        '4' to '٤',
        '5' to '٥',
        '6' to '٦',
        '7' to '٧',
        '8' to '٨',
        '9' to '٩'
    )
    return if (LocalStorageDataSource.getInstance(context).getLanguageCode.contains("ar")) input.map {
        arabicDigits[it] ?: it
    }.joinToString("") else input
}



