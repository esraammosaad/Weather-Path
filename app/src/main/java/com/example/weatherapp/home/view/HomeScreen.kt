package com.example.weatherapp.home.view


import android.content.Context
import android.location.Address
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.home.view_model.HomeViewModelImpl
import com.example.weatherapp.landing.view.AnimatedBackground
import com.example.weatherapp.landing.view.AnimatedPreloader
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.Styles
import com.example.weatherapp.utilis.getTimeFromTimestamp
import com.example.weatherapp.utilis.getWeatherBackground
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.BackgroundAnimation
import com.example.weatherapp.utilis.view.CurrentDateDisplay
import com.example.weatherapp.utilis.view.CustomText
import com.example.weatherapp.utilis.view.FiveDaysWeatherForecastDisplay
import com.example.weatherapp.utilis.view.ImageDisplay
import com.example.weatherapp.utilis.view.LocationDisplay
import com.example.weatherapp.utilis.view.MoreDetailsContainer
import com.example.weatherapp.utilis.view.TemperatureDisplay
import com.example.weatherapp.utilis.view.WeatherForecastDisplay
import com.example.weatherapp.utilis.view.WeatherStatusDisplay


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModelImpl,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel,
    snackBarHostState: SnackbarHostState
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
    val context = LocalContext.current

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
            bottomNavigationBarViewModel.setCurrentWeatherTheme(
                currentWeather.result?.weather?.get(
                    0
                )?.icon ?: ""
            )

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
                    Box() {
                        BackgroundAnimation(getWeatherBackground(currentWeather.result?.weather?.firstOrNull()?.icon?:""))

                        Column {

                            currentWeather.result?.let { ImageDisplay(it) }

                            currentWeather.result?.let {
                                CustomWeatherDetails(
                                    it,
                                    countryName,
                                    context,
                                    fiveDaysWeatherForecast,
                                    listOfDays
                                )
                            }
                        }
                    }

                }


            }

        }
    }

}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomWeatherDetails(
    currentWeather: CurrentWeatherResponse,
    countryName: Response,
    context: Context,
    fiveDaysWeatherForecast: Response,
    listOfDays: List<List<WeatherItem>>
) {




     Column(
         modifier = Modifier
             .fillMaxWidth(),
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.Center

     ) {
         CustomText(text = stringResource(R.string.today))
         Spacer(modifier = Modifier.height(3.dp))
         LastUpdatedDisplay(currentWeather, context)
         Spacer(modifier = Modifier.height(3.dp))
         when (countryName) {
             is Response.Failure -> Text(countryName.exception)
             Response.Loading -> CircularProgressIndicator(color = Color.Black)
             is Response.Success<*> -> {
                 countryName as Response.Success<Address>
                 countryName.result?.let {
                     LocationDisplay(
                         it,
                         currentWeather
                     )
                 }
             }
         }
         Spacer(modifier = Modifier.height(3.dp))
         CurrentDateDisplay()
         Spacer(modifier = Modifier.height(3.dp))
         TemperatureDisplay(currentWeather)
         WeatherStatusDisplay(currentWeather)
         Spacer(modifier = Modifier.height(5.dp))
         Row(
             Modifier
                 .fillMaxWidth()
                 .padding(end = 18.dp),
             horizontalArrangement = Arrangement.Center,
         ) {
             Image(
                 painter = painterResource(R.drawable.temperature),
                 contentDescription = "temp icon"
             )
             Text(
                 stringResource(
                     R.string.feels_like,
                     currentWeather.main.feels_like
                 ),
                 style = Styles.textStyleMedium16,
                 color = Color.White


             )
             Text(
                 stringResource(LocalStorageDataSource.getInstance(context).getTempSymbol),
                 style = Styles.textStyleNormal14,
                 color = Color.White

             )
         }
         Spacer(modifier = Modifier.height(8.dp))
         WeatherForecastDisplay(
             fiveDaysWeatherForecast,
             currentWeather.weather.firstOrNull()?.icon ?: ""
         )
         MoreDetailsContainer(currentWeather)
         FiveDaysWeatherForecastDisplay(
             fiveDaysWeatherForecast = listOfDays
         )

     }
 }


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LastUpdatedDisplay(currentWeather: CurrentWeatherResponse, context: Context) {
    Text(
        text = stringResource(R.string.last_updated) + getTimeFromTimestamp(
            offsetInSeconds = currentWeather.timezone,
            timestamp = currentWeather.dt,
            context = context
        ),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        fontFamily = poppinsFontFamily,
        color = Color.White
    )
}






























