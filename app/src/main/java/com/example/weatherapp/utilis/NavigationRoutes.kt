package com.example.weatherapp.utilis

import com.example.weatherapp.R
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationRoutes(
    val icon: Int
) {

    @Serializable
    object GetStartedScreen : NavigationRoutes(icon = 0)

    @Serializable
    object HomeScreen : NavigationRoutes(icon = R.drawable.baseline_cloud_queue_24)

    @Serializable
    object FavoriteScreen : NavigationRoutes(icon = R.drawable.baseline_map_24)

    @Serializable
    data class WeatherDetailsScreen(var longitude: Double, var latitude: Double) :
        NavigationRoutes(icon = R.drawable.baseline_map_24)

    @Serializable
    object AlarmScreen : NavigationRoutes(icon = R.drawable.baseline_notifications_none_24)

    @Serializable
    object SettingsScreen : NavigationRoutes(icon = R.drawable.baseline_settings_suggest_24)

    @Serializable
    object MapScreen : NavigationRoutes(icon = R.drawable.baseline_map_24)

    @Serializable
    object LocationPickScreen : NavigationRoutes(icon = R.drawable.baseline_map_24)

}