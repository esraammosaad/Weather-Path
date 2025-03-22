package com.example.weatherapp.home.view


import android.location.Address
import android.location.Geocoder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.R
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FivedaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.home.view_model.HomeViewModel
import com.example.weatherapp.home.view_model.HomeViewModelFactory
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.Strings.BASE_IMAGE_URL
import com.example.weatherapp.utilis.convertUnixToTime
import com.example.weatherapp.utilis.formatTime
import com.example.weatherapp.utilis.getCurrentDate
import com.example.weatherapp.utilis.getTheme
import com.example.weatherapp.utilis.isMorning


@Composable
fun HomeScreen(latitude: Double, longitude: Double) {

    val context = LocalContext.current
    val goeCoder = Geocoder(context)


    val viewModel: HomeViewModel = viewModel<HomeViewModel>(
        factory = HomeViewModelFactory(
            Repository.getInstance(
                weatherRemoteDataSource = WeatherRemoteDataSource(),
                weatherLocalDataSource = WeatherLocalDataSource()
            )
        )
    )

    viewModel.getCurrentWeather(latitude = latitude, longitude = longitude)
    viewModel.getCountryName(geocoder = goeCoder, longitude = longitude, latitude = latitude)
    viewModel.getFiveDaysWeatherForecast(latitude = latitude, longitude = longitude)

    val currentWeather = viewModel.currentWeather.observeAsState()
    val fiveDaysWeatherForecast = viewModel.fiveDaysWeatherForecast.observeAsState()
    val message = viewModel.message.observeAsState()
    val countryName = viewModel.countryName.observeAsState()

    Scaffold(

        contentWindowInsets = WindowInsets(0.dp),

        ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush =
                    getTheme()
                )
        ) {

            item {
                ImageDisplay()

                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

                ) {

                    CustomText(text = "TODAY")
                    Spacer(modifier = Modifier.height(3.dp))
                    LocationDisplay(countryName)
                    Spacer(modifier = Modifier.height(5.dp))
                    CurrentDateDisplay()
                    Spacer(modifier = Modifier.height(5.dp))
                    TemperatureDisplay(currentWeather)
                    WeatherStatusDisplay(currentWeather)
                    fiveDaysWeatherForecast.value?.let { WeatherForecastDisplay(it) }
                    currentWeather.value?.let { MoreDetailsContainer(it) }
                }

            }


        }


    }
}

@Composable
fun MoreDetailsContainer(currentWeather: CurrentWeatherResponse) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {

        Box(
            Modifier
                .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(15.dp))
                .fillMaxWidth()
                .height(190.dp)

        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 5.dp).fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(end = 20.dp)) {
                    MoreDetailsItem(
                        icon = R.drawable.sunrise,
                        textOne = "Sunrise",
                        textTwo = convertUnixToTime(currentWeather.sys.sunrise.toLong())
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    MoreDetailsItem(
                        icon = R.drawable.wind,
                        textOne = "Wind",
                        textTwo = currentWeather.wind.speed.toString() + " m/s"
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    MoreDetailsItem(
                        icon = R.drawable.thermometer,
                        textOne = "Pressure",
                        textTwo = currentWeather.main.pressure.toString() + " hPa"
                    )


                }

                Spacer(modifier = Modifier.width(20.dp))
                VerticalDivider(modifier = Modifier
                    .height(180.dp)
                    .width(2.dp), color = Color.Gray)
                Spacer(modifier = Modifier.width(20.dp))
                Column(modifier = Modifier.padding(start = 20.dp)) {
                    MoreDetailsItem(
                        icon = R.drawable.sunset,
                        textOne = "Sunset",
                        textTwo = convertUnixToTime(currentWeather.sys.sunset.toLong())
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    MoreDetailsItem(
                        icon = R.drawable.humidity,
                        textOne = "Humidity",
                        textTwo = currentWeather.main.humidity.toString() + " %"
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    MoreDetailsItem(
                        icon = R.drawable.cloud_sketched,
                        textOne = "Clouds",
                        textTwo = currentWeather.clouds.all.toString() + " %"
                    )

                }


            }


        }
    }


}

@Composable
private fun MoreDetailsItem(icon: Int, textOne: String, textTwo: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(icon), contentDescription = "wind icon")
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {

            CustomText(textOne)
            CustomText(textTwo)

        }

    }
}

@Composable
private fun WeatherForecastDisplay(fiveDaysWeatherForecast: FivedaysWeatherForecastResponse) {


    LazyRow(modifier = Modifier.padding(horizontal = 8.dp, vertical = 32.dp)) {


        items(fiveDaysWeatherForecast.list.size) { index: Int ->
            WeatherForecastItem(fiveDaysWeatherForecast.list[index])

        }


    }

}

@Composable
private fun WeatherForecastItem(weatherItem: WeatherItem) {

    Column(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(100.dp)
            .border(
                width = 1.dp,
                brush = getTheme(),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CustomText(formatTime(weatherItem.dt_txt))
        WeatherStatusImageDisplay(
            weatherItem.weather[0].icon
        )
        Row {
            Text(
                weatherItem.main.temp.toString(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                fontFamily = poppinsFontFamily,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                Strings.CELSIUS_SYMBOL,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                fontFamily = poppinsFontFamily,
                color = Color.White
            )
        }


    }


}

@Composable
private fun CustomText(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        fontFamily = poppinsFontFamily,
        color = Color.White,

        )
}

@Composable
private fun CurrentDateDisplay() {
    Text(
        text = getCurrentDate(),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        fontFamily = poppinsFontFamily,
        color = Color.White
    )
}

@Composable
private fun ImageDisplay() {
    Image(
        painter = painterResource(
            if (isMorning()) R.drawable.sun
            else R.drawable.moon
        ),
        contentDescription = stringResource(R.string.sun_or_moon_icon),
        modifier = Modifier
            .size(150.dp)
            .padding(top = 36.dp, end = 16.dp)
    )
}

@Composable
private fun LocationDisplay(countryName: State<Address?>) {
    Text(
        countryName.value?.locality ?: "",
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        fontFamily = poppinsFontFamily,
        color = Color.White

    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
        countryName.value?.countryName ?: "",
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        fontFamily = poppinsFontFamily,
        color = OffWhite

    )

}

@Composable
private fun WeatherStatusDisplay(currentWeather: State<CurrentWeatherResponse?>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(start = 12.dp)
    ) {
        Text(
            text = currentWeather.value?.weather?.get(0)?.description ?: "",
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            fontFamily = poppinsFontFamily,
            color = OffWhite
        )
        WeatherStatusImageDisplay(
            currentWeather.value?.weather?.get(
                0
            )?.icon ?: ""
        )
    }
}

@Composable
private fun WeatherStatusImageDisplay(icon: String) {
    Image(
        painter = rememberAsyncImagePainter(
            "${BASE_IMAGE_URL}${
                icon
            }.png"
        ),
        contentDescription = stringResource(R.string.sun_icon),
        modifier = Modifier
            .height(30.dp)
            .width(30.dp)
            .padding(top = 3.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun TemperatureDisplay(currentWeather: State<CurrentWeatherResponse?>) {
    Row {
        Text(
            currentWeather.value?.main?.temp.toString(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 70.sp,
            fontFamily = poppinsFontFamily,
            modifier = Modifier.padding(start = 14.dp),
            color = Color.White
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            Strings.CELSIUS_SYMBOL,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            fontFamily = poppinsFontFamily,
            color = Color.White
        )
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun HomeScreenPreview() {

    HomeScreen(
        longitude = 0.0, latitude = 0.0,
    )


}