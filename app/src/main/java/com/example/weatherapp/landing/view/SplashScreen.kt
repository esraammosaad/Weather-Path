package com.example.weatherapp.landing.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.getWeatherGradient
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(500)
        visible = true
        delay(7000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getWeatherGradient()),
        contentAlignment = Alignment.Center
    ) {
        AnimatedPreloader()
        Spacer(modifier = Modifier.height(90.dp))
        AnimatedVisibility(
            modifier = Modifier.align(alignment = Alignment.Center),
            visible = visible,
            enter = androidx.compose.animation.expandHorizontally(
                animationSpec = tween(
                    durationMillis = 1000
                )
            ),
            exit = androidx.compose.animation.fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            Text(
                text = stringResource(id = R.string.weather_path),
                style = TextStyle(
                    fontSize = 35.sp,
                    color = PrimaryColor,
                    textAlign = TextAlign.Center,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(top = 160.dp)
                    .align(alignment = Alignment.Center)
            )


        }
    }
}


@Composable
fun AnimatedPreloader() {
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.animated_icon
        )
    )
    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )
    LottieAnimation(
        modifier = Modifier.padding(bottom = 80.dp),
        composition = preloaderLottieComposition,
        progress = preloaderProgress,
    )
}