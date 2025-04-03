package com.example.weatherapp.utilis.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.Styles
import com.example.weatherapp.utilis.secondFormatDateTime

@Composable
fun FiveDaysWeatherForecastDisplay(fiveDaysWeatherForecast: List<List<WeatherItem>?>) {

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .padding(end = 12.dp, start = 12.dp, top = 22.dp)
            .size(450.dp),
    ) {

        item {
            Text(
                stringResource(R.string._5_day_forecast),
                style = Styles.textStyleSemiBold22,
                color = Color.White

            )
            Spacer(modifier = Modifier.height(8.dp))
        }
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
                            text = if (dayIndex == 0) stringResource(R.string.todaySmall) else
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
                        Row {
                            Text(
                                text = "${fiveDaysWeatherForecast[dayIndex]?.get(0)?.main?.temp_min?.toInt()}",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.5f),
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(2.dp))

                            Text(
                                text = stringResource(LocalStorageDataSource.getInstance(context).getTempSymbol),
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.5f),
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Medium
                            )

                        }

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