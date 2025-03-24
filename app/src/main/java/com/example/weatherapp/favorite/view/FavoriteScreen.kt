package com.example.weatherapp.favorite.view

import android.location.Address
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.weatherapp.home.view_model.HomeViewModelImpl
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.Strings.BASE_IMAGE_URL
import com.example.weatherapp.utilis.getWeatherGradient


@Composable
fun FavoriteScreen(viewModel: HomeViewModelImpl?, onMapClick: () -> Unit) {

    val currentWeather = viewModel?.currentWeather?.observeAsState()?.value
    val countryName = viewModel?.countryName?.observeAsState()?.value


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = getWeatherGradient(
                    currentWeather?.weather?.get(0)?.icon ?: ""
                )
            ),
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 50.dp, horizontal = 16.dp)
                .fillMaxWidth()
        ) {

            Text(
                "Locations", textAlign = TextAlign.Center,
                fontSize = 26.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontFamily = poppinsFontFamily

            )
            Image(
                painter = painterResource(R.drawable.baseline_map_24),
                contentDescription = stringResource(
                    R.string.map_icon,

                    ),
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        onMapClick.invoke()

                    }
            )
        }


        FavoriteLazyColumn(currentWeather, countryName)


//        MapScreen (viewModel)

    }

}

@Composable
fun FavoriteLazyColumn(currentWeather: CurrentWeatherResponse?, countryName: Address?) {

    LazyColumn(modifier = Modifier.padding(16.dp)) {

        item {
            FavoriteItem(currentWeather, countryName)

        }
    }


}

@Composable
fun FavoriteItem(currentWeather: CurrentWeatherResponse?, countryName: Address?) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = getWeatherGradient(currentWeather?.weather?.get(0)?.icon ?: ""),
                shape = RoundedCornerShape(25.dp)
            )
            .padding(28.dp),
        elevation = CardDefaults.cardElevation(),
        colors = CardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White
        )
    ) {

        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.height(200.dp)) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Column {
                    Text(
                        text = "Current Location",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppinsFontFamily,
                        fontSize = 20.sp
                    )
                    Text(
                        text = countryName?.locality.toString(),
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFontFamily,
                        fontSize = 18.sp

                    )
                }
                Text(
                    text = currentWeather?.main?.temp.toString() + Strings.CELSIUS_SYMBOL,
                    fontWeight = FontWeight.Medium,
                    fontFamily = poppinsFontFamily,
                    fontSize = 30.sp

                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = currentWeather?.weather?.get(0)?.description ?: "",
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFontFamily,
                        fontSize = 18.sp

                    )
                    Image(
                        painter = rememberAsyncImagePainter(
                            "$BASE_IMAGE_URL${
                                currentWeather?.weather?.get(0)?.icon
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


                Text(
                    text = "H:${currentWeather?.main?.temp_max}${Strings.CELSIUS_SYMBOL}  L:${currentWeather?.main?.temp_min}${Strings.CELSIUS_SYMBOL}",
                    fontWeight = FontWeight.Medium,
                    fontFamily = poppinsFontFamily,
                    fontSize = 16.sp

                )

            }
        }

    }


}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FavoriteScreenPreview() {

    FavoriteScreen(null) {}


}


