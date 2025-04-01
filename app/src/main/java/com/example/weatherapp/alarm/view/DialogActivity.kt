package com.example.weatherapp.alarm.view

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.remote.RetrofitFactory
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.favorite.view_model.FavoriteViewModelFactory
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.example.weatherapp.home.view_model.HomeViewModelFactory
import com.example.weatherapp.home.view_model.HomeViewModelImpl
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.ui.theme.poppinsFontFamily
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.WeatherStatusImageDisplay
import com.google.gson.Gson

class DialogActivity : ComponentActivity() {
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
        val mediaPlayer = MediaPlayer.create(this@DialogActivity, R.raw.sound_track_two)
        mediaPlayer.start()
        setContent {
            val favoriteViewModelImpl  = ViewModelProvider(
                this,
                FavoriteViewModelFactory(
                    Repository.getInstance(
                        weatherRemoteDataSource = WeatherRemoteDataSource(RetrofitFactory.apiService),
                        weatherLocalDataSource = WeatherLocalDataSource(
                            WeatherDatabase.getInstance(
                                this
                            ).getDao()
                        )
                    )
                )
            )[FavoriteViewModelImpl::class]

            AlertScreen(response, onConfirmClicked = {
                favoriteViewModelImpl.deleteAlarm(response.id)
                finish()

            }, onOpenAppClicked = {
                favoriteViewModelImpl.deleteAlarm(response.id)
                finish()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            })


        }
    }

}

@Composable
private fun AlertScreen(
    currentWeatherResponse: CurrentWeatherResponse,
    onConfirmClicked: () -> Unit,
    onOpenAppClicked: () -> Unit
) {
    val context = LocalContext.current
    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
        Box(
            modifier = Modifier
                .background(
                    brush = getWeatherGradient(
                        currentWeatherResponse.weather.firstOrNull()?.icon ?: ""
                    ), shape = RoundedCornerShape(15.dp)
                )
                .padding(24.dp)
                .height(210.dp)
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
                            currentWeatherResponse.name,
                            currentWeatherResponse.weather.firstOrNull()?.description ?: "",
                            currentWeatherResponse.main.temp
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
                            onOpenAppClicked.invoke()
                        }, colors = ButtonColors(
                            containerColor = PrimaryColor,
                            contentColor = Color.White,
                            disabledContainerColor = PrimaryColor,
                            disabledContentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            stringResource(R.string.open_app),
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
                            stringResource(R.string.confirm),
                            fontFamily = poppinsFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                        )
                    }
                }

            }
        }
    }
}