package com.example.weatherapp.home.view


import android.location.Address
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.R
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.home.view_model.HomeViewModelImpl
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.Strings.BASE_IMAGE_URL
import com.example.weatherapp.utilis.convertUnixToTime
import com.example.weatherapp.utilis.formatTime
import com.example.weatherapp.utilis.getCurrentDate
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.isMorning
import com.example.weatherapp.utilis.secondFormatDateTime


@Composable
fun HomeScreen(viewModel: HomeViewModelImpl) {

    val currentWeather = viewModel.currentWeather.observeAsState()
    val fiveDaysWeatherForecast = viewModel.fiveDaysWeatherForecast.observeAsState()
    val currentDayWeatherForecast = viewModel.currentDayList.observeAsState()
    val nextDayWeatherForecast = viewModel.nextDayList.observeAsState()
    val thirdDayWeatherForecast = viewModel.thirdDayList.observeAsState()
    val fourthDayWeatherForecast = viewModel.fourthDayList.observeAsState()
    val fifthDayWeatherForecast = viewModel.fifthDayList.observeAsState()
    val sixthDayWeatherForecast = viewModel.sixthDayList.observeAsState()
    val message = viewModel.message.observeAsState()
    val countryName = viewModel.countryName.observeAsState()

    val listOfDays = listOf(
        currentDayWeatherForecast.value,
        nextDayWeatherForecast.value,
        thirdDayWeatherForecast.value,
        fourthDayWeatherForecast.value,
        fifthDayWeatherForecast.value,
        sixthDayWeatherForecast.value
    )



    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush =
                getWeatherGradient(
                    currentWeather.value?.weather?.get(
                        0
                    )?.icon ?: ""
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
                LocationDisplay(countryName)
                Spacer(modifier = Modifier.height(5.dp))
                CurrentDateDisplay()
                Spacer(modifier = Modifier.height(5.dp))
                TemperatureDisplay(currentWeather)
                WeatherStatusDisplay(currentWeather)
                fiveDaysWeatherForecast.value?.let {
                    WeatherForecastDisplay(
                        it,
                        currentWeather.value?.weather?.get(0)?.icon ?: ""
                    )
                }
                currentWeather.value?.let { MoreDetailsContainer(it) }
                FiveDaysWeatherForecastDisplay(
                    fiveDaysWeatherForecast = listOfDays
                )

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
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth()
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
                VerticalDivider(
                    modifier = Modifier
                        .height(180.dp)
                        .width(1.dp), color = Color.Gray.copy(alpha = 0.5f)
                )
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
private fun FiveDaysWeatherForecastDisplay(fiveDaysWeatherForecast: List<List<WeatherItem>?>) {

    LazyColumn(
        modifier = Modifier
            .padding(end = 12.dp, start = 12.dp, top = 16.dp)
            .size(450.dp),
    ) {
        items(fiveDaysWeatherForecast.size) {

                dayIndex ->

            if (!fiveDaysWeatherForecast[dayIndex].isNullOrEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (dayIndex == 0) "Today" else
                                fiveDaysWeatherForecast[dayIndex]?.get(0)?.dt_txt?.let {
                                    secondFormatDateTime(
                                        it
                                    )
                                } ?: "",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            fontFamily = poppinsFontFamily,
                            color = Color.White,

                            )
                        WeatherStatusImageDisplay(
                            fiveDaysWeatherForecast[dayIndex]?.get(0)?.weather?.get(
                                0
                            )?.icon ?: ""
                        )
                    }


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "${fiveDaysWeatherForecast[dayIndex]?.get(0)?.main?.temp_max?.toInt()}",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "/",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Medium

                        )
                        Text(
                            text = "${fiveDaysWeatherForecast[dayIndex]?.get(0)?.main?.temp_min?.toInt()}${Strings.CELSIUS_SYMBOL}",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Medium
                        )

                    }


                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {


                    items(fiveDaysWeatherForecast[dayIndex]?.size ?: 0) { index ->
                        fiveDaysWeatherForecast[dayIndex]?.get(index)?.let { DayWeatherItem(it) }
                    }

                }
            }
        }
    }


}

@Composable
private fun DayWeatherItem(weatherItem: WeatherItem) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .padding(end = 10.dp)
                .size(60.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            WeatherStatusImageDisplay(
                weatherItem.weather[0].icon
            )
            Text(
                text = "${weatherItem.main.temp_min.toInt()}${Strings.CELSIUS_SYMBOL}",
                fontSize = 14.sp,
                color = Color.White,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Medium
            )

        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = formatTime(weatherItem.dt_txt),
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            fontFamily = poppinsFontFamily,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(end = 9.dp)

        )

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
private fun WeatherForecastDisplay(
    fiveDaysWeatherForecast: List<WeatherItem>,
    icon: String
) {

    LazyRow(modifier = Modifier.padding(horizontal = 12.dp, vertical = 32.dp)) {
        items(fiveDaysWeatherForecast.size) { index: Int ->
            WeatherForecastItem(fiveDaysWeatherForecast[index], icon)
        }
    }

}

@Composable
private fun WeatherForecastItem(weatherItem: WeatherItem, icon: String) {

    Column(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(100.dp)
            .border(
                width = 1.dp,
                brush = getWeatherGradient(icon),
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
        WeatherForecastItemTemperature(weatherItem)


    }


}

@Composable
private fun WeatherForecastItemTemperature(weatherItem: WeatherItem) {
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
        text = getCurrentDate(0),
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
private fun HomeScreenPreview(
    homeViewModel: HomeViewModelImpl? = null,
) {
    HomeScreen(
        homeViewModel!!
    )
}