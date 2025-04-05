package com.example.weatherapp.utilis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.weatherapp.R
import com.example.weatherapp.landing.view.AnimatedBackground

@Composable
fun BackgroundAnimation(weatherBackground: Int) {
    Column(Modifier.fillMaxSize()) {

        val i = if (weatherBackground == R.raw.rain) {
            11
        } else {
            6
        }

        for (j in 0..i) {
            AnimatedBackground(weatherBackground)


        }


    }
}