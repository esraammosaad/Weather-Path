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
        if(weatherBackground == R.raw.rain){
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
        }else{
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
            AnimatedBackground(weatherBackground)
        }


    }
}