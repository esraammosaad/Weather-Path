package com.example.weatherapp.utilis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.weatherapp.R

@Composable
fun BackgroundAnimation(weatherBackground: Int) {
    Column(Modifier.fillMaxSize()) {
        val i = if (weatherBackground == R.raw.rain) {
            10
        } else {
            5
        }
        for (j in 0..i) {
            AnimatedPreloader(weatherBackground)
        }
    }
}