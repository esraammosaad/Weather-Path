package com.example.weatherapp.internet

import kotlinx.coroutines.flow.Flow

interface InternetObserver {

    val isConnected : Flow<Boolean>
}