package com.example.weatherapp.utilis

import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationRoutes {

    @Serializable
    object GetStartedScreen : NavigationRoutes()

    @Serializable
    object HomeScreen : NavigationRoutes()

}