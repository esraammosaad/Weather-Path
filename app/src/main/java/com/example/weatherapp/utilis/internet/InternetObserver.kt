package com.example.weatherapp.utilis.internet

import kotlinx.coroutines.flow.Flow

interface InternetObserver {

    val isConnected : Flow<Boolean>
}