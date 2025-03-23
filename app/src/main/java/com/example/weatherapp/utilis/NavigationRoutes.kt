package com.example.weatherapp.utilis

import com.example.weatherapp.R
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationRoutes(
    val title: String,
    val icon: Int
) {

    @Serializable
    object GetStartedScreen : NavigationRoutes(title = "GetStarted", icon = 0)

    @Serializable
    object HomeScreen : NavigationRoutes(title = "Weather", icon = R.drawable.baseline_cloud_queue_24)

    @Serializable
    object FavoriteScreen :
        NavigationRoutes(title = "Favorite", icon = R.drawable.baseline_favorite_border_24)

    @Serializable
    object AlarmScreen :
        NavigationRoutes(title = "Alarm", icon = R.drawable.baseline_notifications_none_24)

    @Serializable
    object SettingsScreen :
        NavigationRoutes(title = "Preferences", icon = R.drawable.baseline_settings_suggest_24)

}