package com.example.weatherapp.home.view


import android.location.Address
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.home.view_model.HomeViewModelImpl
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.CurrentDateDisplay
import com.example.weatherapp.utilis.view.CustomText
import com.example.weatherapp.utilis.view.FiveDaysWeatherForecastDisplay
import com.example.weatherapp.utilis.view.ImageDisplay
import com.example.weatherapp.utilis.view.LocationDisplay
import com.example.weatherapp.utilis.view.MoreDetailsContainer
import com.example.weatherapp.utilis.view.TemperatureDisplay
import com.example.weatherapp.utilis.view.WeatherForecastDisplay
import com.example.weatherapp.utilis.view.WeatherStatusDisplay


@Composable
fun HomeScreen(
    viewModel: HomeViewModelImpl,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel
) {

    val currentWeather = viewModel.currentWeather.collectAsStateWithLifecycle().value
    val fiveDaysWeatherForecast =
        viewModel.fiveDaysWeatherForecast.collectAsStateWithLifecycle().value
    val currentDayWeatherForecast = viewModel.currentDayList.collectAsStateWithLifecycle()
    val nextDayWeatherForecast = viewModel.nextDayList.collectAsStateWithLifecycle()
    val thirdDayWeatherForecast = viewModel.thirdDayList.collectAsStateWithLifecycle()
    val fourthDayWeatherForecast = viewModel.fourthDayList.collectAsStateWithLifecycle()
    val fifthDayWeatherForecast = viewModel.fifthDayList.collectAsStateWithLifecycle()
    val sixthDayWeatherForecast = viewModel.sixthDayList.collectAsStateWithLifecycle()
    val message = viewModel.message.collectAsStateWithLifecycle().value
    val countryName = viewModel.countryName.collectAsStateWithLifecycle().value

    val listOfDays = listOf(
        currentDayWeatherForecast.value,
        nextDayWeatherForecast.value,
        thirdDayWeatherForecast.value,
        fourthDayWeatherForecast.value,
        fifthDayWeatherForecast.value,
        sixthDayWeatherForecast.value
    )



    when (currentWeather) {
        is Response.Failure -> Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(currentWeather.exception)

        }

        Response.Loading -> Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = Color.White)

        }

        is Response.Success<*> -> {
            currentWeather as Response.Success<CurrentWeatherResponse>
            bottomNavigationBarViewModel.setCurrentWeatherTheme(currentWeather.result?.weather?.get(0)?.icon?:"")
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush =
                        getWeatherGradient(
                            currentWeather.result?.weather?.firstOrNull()?.icon ?: ""
                        )
                    )
            ) {

                item {
                    ImageDisplay()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center

                    ) {

                        CustomText(text = "TODAY")
                        Spacer(modifier = Modifier.height(3.dp))
                        when (countryName) {
                            is Response.Failure -> Text(countryName.exception)
                            Response.Loading -> CircularProgressIndicator(color = Color.Black)
                            is Response.Success<*> -> {
                                countryName as Response.Success<Address>
                                countryName.result?.let {
                                    currentWeather.result?.let { it1 ->
                                        LocationDisplay(
                                            it,
                                            it1
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        CurrentDateDisplay()
                        Spacer(modifier = Modifier.height(5.dp))
                        currentWeather.result?.let { TemperatureDisplay(it) }
                        currentWeather.result?.let { WeatherStatusDisplay(it) }
                        WeatherForecastDisplay(
                            fiveDaysWeatherForecast,
                            currentWeather.result?.weather?.firstOrNull()?.icon ?: ""
                        )
                        currentWeather.result?.let { MoreDetailsContainer(it) }
                        FiveDaysWeatherForecastDisplay(
                            fiveDaysWeatherForecast = listOfDays
                        )

                    }

                }


            }

        }
    }

}






























