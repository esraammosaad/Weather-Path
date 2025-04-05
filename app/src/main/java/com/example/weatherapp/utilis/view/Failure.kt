package com.example.weatherapp.utilis.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.data.model.Response

@Composable
fun Failure(currentWeatherUiState: Response.Failure, isConnected: Boolean) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isConnected)
                currentWeatherUiState.exception
            else
                context.getString(R.string.no_internet_connection),
            fontSize = 18.sp

        )
    }
}
