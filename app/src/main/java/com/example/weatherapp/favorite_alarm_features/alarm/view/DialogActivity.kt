package com.example.weatherapp.favorite_alarm_features.alarm.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSourceImpl
import com.example.weatherapp.data.managers.WorkManagerHelper
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.remote.RetrofitFactory
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteViewModelFactory
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteAndAlarmSharedViewModelImpl
import com.example.weatherapp.home_settings_feature.home.view.LastUpdatedDisplay
import com.example.weatherapp.landing.view.AnimatedBackground
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.convertToArabicNumbers
import com.example.weatherapp.utilis.getWeatherBackground
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.WeatherStatusImageDisplay
import com.google.gson.Gson
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DialogActivity : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    @RequiresApi(Build.VERSION_CODES.O)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawable(ColorDrawable(0))
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val result = intent.getStringExtra(Strings.RESULT_CONST)
        val gson = Gson()
        val response = gson.fromJson(result, CurrentWeatherResponse::class.java)
        mediaPlayer = MediaPlayer.create(this@DialogActivity, R.raw.tropical_alarm_clock)
        mediaPlayer.start()
        val favoriteViewModelImpl = ViewModelProvider(
            this,
            FavoriteViewModelFactory(
                WeatherRepositoryImpl.getInstance(
                    weatherRemoteDataSourceImpl = WeatherRemoteDataSourceImpl(RetrofitFactory.apiService),
                    weatherLocalDataSourceImpl = WeatherLocalDataSourceImpl(
                        WeatherDatabase.getInstance(
                            this
                        ).getDao()
                    )
                )
            )
        )[FavoriteAndAlarmSharedViewModelImpl::class]

        setContent {
            AlertScreen(
                response,
                onConfirmClicked = {
                    finish()
                },
                onSnoozeClicked = {
                    snoozeAlarm(response, favoriteViewModelImpl, this@DialogActivity, "Alert")
                    finish()
                },
                onOpenAppClicked = {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                })
        }
    }


    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AlertScreen(
    currentWeatherResponse: CurrentWeatherResponse,
    onConfirmClicked: () -> Unit,
    onSnoozeClicked: () -> Unit,
    onOpenAppClicked: () -> Unit
) {
    val context = LocalContext.current
    Box(contentAlignment = Alignment.Center, modifier = Modifier
        .padding(12.dp)
        .clickable {
            onOpenAppClicked.invoke()
        }) {
        Box(
            modifier = Modifier
                .background(
                    brush = getWeatherGradient(
                        currentWeatherResponse.weather.firstOrNull()?.icon ?: ""
                    ), shape = RoundedCornerShape(15.dp)
                )
                .height(260.dp)
        ) {

            Column(Modifier.fillMaxWidth()) {
                if (getWeatherBackground(
                        currentWeatherResponse.weather.firstOrNull()?.icon ?: ""
                    ) == R.raw.rain
                ) {
                    AnimatedBackground(
                        getWeatherBackground(
                            currentWeatherResponse.weather.firstOrNull()?.icon ?: ""
                        )
                    )
                    AnimatedBackground(
                        getWeatherBackground(
                            currentWeatherResponse.weather.firstOrNull()?.icon ?: ""
                        )
                    )
                } else {
                    AnimatedBackground(
                        getWeatherBackground(
                            currentWeatherResponse.weather.firstOrNull()?.icon ?: ""
                        )
                    )

                }

            }


            Box(
                Modifier.padding(24.dp)
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.s_weather, currentWeatherResponse.cityName),
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = Color.White,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text(
                            text = currentWeatherResponse.weather.firstOrNull()?.description
                                ?: "",
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp,
                            fontFamily = poppinsFontFamily,
                            color = OffWhite
                        )
                        WeatherStatusImageDisplay(
                            currentWeatherResponse.weather[0].icon
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text(
                            stringResource(
                                R.string.today_s_weather_in_is_with_a_temperature_of,
                                currentWeatherResponse.cityName,
                                currentWeatherResponse.weather.firstOrNull()?.description ?: "",
                               convertToArabicNumbers( currentWeatherResponse.main.temp.toString(),context)
                            ) + stringResource(LocalStorageDataSource.getInstance(context).getTempSymbol) + " " + stringResource(
                                R.string.stay_prepared_and_enjoy_your_day
                            ),
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Button(
                            onClick = {
                                onSnoozeClicked.invoke()
                            }, colors = ButtonColors(
                                containerColor = PrimaryColor,
                                contentColor = Color.White,
                                disabledContainerColor = PrimaryColor,
                                disabledContentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                stringResource(R.string.snooze),
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                onConfirmClicked.invoke()
                            },
                            colors = ButtonColors(
                                containerColor = OffWhite,
                                contentColor = Color.Black,
                                disabledContainerColor = OffWhite,
                                disabledContentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, getWeatherGradient())
                        ) {
                            Text(
                                stringResource(R.string.confirmCapital),
                                fontFamily = poppinsFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    LastUpdatedDisplay(currentWeatherResponse, context)
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun snoozeAlarm(
    response: CurrentWeatherResponse?,
    favoriteViewModelImpl: FavoriteAndAlarmSharedViewModelImpl,
    context: Context,
    alarmType: String
) {
    val newTime = LocalTime.now().plusMinutes(10)
    val formattedTime = newTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    val currentDate = LocalDate.now()
    val formattedDate =
        currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val alarm = AlarmModel(
        locationId = response?.id ?: 0,
        date = formattedDate,
        time = formattedTime,
        countryName = response?.countryName ?: "",
        cityName = response?.cityName ?: "",
        alarmType = alarmType,
        longitude = response?.longitude ?: 0.0,
        latitude = response?.latitude ?: 0.0
    )
    favoriteViewModelImpl.insertAlarm(alarm)
    WorkManagerHelper.requestWorkManagerForSet(
        response,
        context,
        10 * 60 * 1000L
    )
}