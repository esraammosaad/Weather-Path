package com.example.weatherapp.home.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.R
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.home.view_model.HomeViewModel
import com.example.weatherapp.home.view_model.HomeViewModelFactory

@Composable
fun HomeScreen() {

    val viewModel: HomeViewModel = viewModel<HomeViewModel>(
        factory = HomeViewModelFactory(
            Repository.getInstance(
                weatherRemoteDataSource = WeatherRemoteDataSource(),
                weatherLocalDataSource = WeatherLocalDataSource()
            )
        )
    )

    val currentWeather = viewModel.currentWeather.observeAsState()
    val message = viewModel.message.observeAsState()


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message.value.toString(), modifier = Modifier.clickable {

            viewModel.getCurrentWeather(0.0, 0.0)
            Log.i("TAG", "HomeScreen: ${currentWeather.value}")
        })


    }

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun HomeScreenPreview() {

    Column(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.sun),
            contentDescription = stringResource(R.string.sun_icon)
        )


    }


}