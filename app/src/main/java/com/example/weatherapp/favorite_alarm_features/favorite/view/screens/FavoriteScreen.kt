package com.example.weatherapp.favorite_alarm_features.favorite.view.screens

import android.content.Context
import android.location.Address
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkManager
import com.example.weatherapp.R
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.favorite_alarm_features.favorite.view.components.FavoriteLazyColumn
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteAndAlarmSharedViewModelImpl
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.Styles
import com.example.weatherapp.utilis.getWeatherGradient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun FavoriteScreen(
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl,
    currentWeather: CurrentWeatherResponse,
    countryName: Address,
    onMapClick: () -> Unit,
    onFavoriteCardClicked: (longitude: Double, latitude: Double) -> Unit,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel,
    snackBarHostState: SnackbarHostState,
    isConnected: Boolean
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        favoriteViewModel.message.collect {
            snackBarHostState.showSnackbar(context.getString(it))
        }
    }
    LaunchedEffect(Unit) {
        favoriteViewModel.selectFavorites()
        favoriteViewModel.selectAllAlarms()
    }
    val weatherFavorites by remember { favoriteViewModel.weatherFavorites }.collectAsStateWithLifecycle()
    val alarms by remember { favoriteViewModel.alarms }.collectAsStateWithLifecycle()
    bottomNavigationBarViewModel.setCurrentWeatherTheme(
        currentWeather.weather.firstOrNull()?.icon ?: ""
    )
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = getWeatherGradient(
                    currentWeather.weather.firstOrNull()?.icon ?: ""
                )
            ),
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 50.dp, end = 16.dp, start = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.locations), textAlign = TextAlign.Center,
                        style = Styles.textStyleSemiBold26
                    )
                    Image(
                        painter = painterResource(R.drawable.baseline_map_24),
                        contentDescription = stringResource(
                            R.string.map_icon
                        ),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                onMapClick.invoke()
                            }
                    )
                }
                FavoriteLazyColumn(
                    weatherFavorites,
                    currentWeather,
                    countryName.countryName ?: currentWeather.countryName.takeIf { it.isNotEmpty() }
                    ?: currentWeather.sys.country,
                    favoriteViewModel,
                    onFavoriteCardClicked,
                    snackBarHostState,
                    alarms,
                    coroutineScope,
                )
            }
        }
    }

}


fun deleteFavoriteItem(
    item: CurrentWeatherResponse?,
    fiveDaysForecastFavorites: List<FiveDaysWeatherForecastResponse>?,
    index: Int,
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    context: Context,
    selectedAlarmModel: AlarmModel?
) {
    if (item != null) {
        fiveDaysForecastFavorites?.getOrNull(index)
            ?.let {
                favoriteViewModel.deleteWeather(
                    fiveDaysWeatherForecastResponse = it,
                    currentWeatherResponse = item
                )
                if (selectedAlarmModel != null) {
                    deleteAlarm(
                        selectedAlarm = selectedAlarmModel,
                        context = context,
                        favoriteViewModel = favoriteViewModel,
                        coroutineScope = coroutineScope,
                        snackBarHostState = snackBarHostState
                    )
                }
                coroutineScope.launch {
                    val result = snackBarHostState.showSnackbar(
                        message = context.getString(R.string.location_deleted_successfully),
                        actionLabel = context.getString(R.string.undo),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long,
                    )
                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            favoriteViewModel.insertWeather(
                                currentWeatherResponse = item,
                                fiveDaysWeatherForecastResponse = it
                            )
                        }

                        SnackbarResult.Dismissed -> {
                        }
                    }

                }
            }
    }
}


fun deleteAlarm(
    selectedAlarm: AlarmModel?,
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl,
    context: Context,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState
) {
    if (selectedAlarm != null) {
        favoriteViewModel.deleteAlarm(selectedAlarm.locationId)
        WorkManager.getInstance(context)
            .cancelAllWorkByTag(selectedAlarm.locationId.toString())
//        coroutineScope.launch {
//            snackBarHostState.showSnackbar(
//                message = context.getString(R.string.alarm_reset_successfully),
//                withDismissAction = true,
//                duration = SnackbarDuration.Long,
//            )
//
//        }
    }
}


//    val swipeToDismissState = rememberSwipeToDismissBoxState(
//       confirmValueChange = { state ->
//     if (state == SwipeToDismissBoxValue.EndToStart) {
//    scope.launch {
//     delay(500)
//     deleteFavoriteItem(
//     item,
//    fiveDaysForecastFavorites,
//    index,
//   favoriteViewModel,
//   coroutineScope,
// snackBarHostState,
//  )
//   }
//   true
//   } else {
//    false
//   }
//  }
//   )


// SwipeToDismissBox(
//    state = swipeToDismissState,
//     backgroundContent = {
//       SwipeToDismissBackGround()
//         },
//  modifier = Modifier.animateItemPlacement()
//   ) {}


//@Composable
//private fun SwipeToDismissBackGround() {
//    Box(
//        modifier = Modifier
//            .padding(bottom = 12.dp)
//            .fillMaxWidth()
//            .height(200.dp)
//            .background(
//                color = animateColorAsState(targetValue = Color.Red).value,
//                shape = RoundedCornerShape(25.dp)
//            )
//            .padding(28.dp),
//        contentAlignment = Alignment.CenterEnd
//    ) {
//        Icon(
//            Icons.Default.Delete,
//            contentDescription = stringResource(R.string.delete_icon),
//            tint = Color.White
//        )
//    }
//}



