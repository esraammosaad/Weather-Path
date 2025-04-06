package com.example.weatherapp.favorite_alarm_features.alarm.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.favorite_alarm_features.favorite.view.deleteAlarm
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteAndAlarmSharedViewModelImpl
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.Styles
import com.example.weatherapp.utilis.convertToArabicNumbers
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.AnimatedPreloader
import com.example.weatherapp.utilis.view.ConfirmationDialog
import com.example.weatherapp.utilis.view.FailureDisplay
import com.example.weatherapp.utilis.view.LoadingDisplay


@Composable
fun AlarmScreen(
    favoriteViewModel: FavoriteAndAlarmSharedViewModelImpl,
    currentWeather: CurrentWeatherResponse,
    snackBarHostState: SnackbarHostState,
    navigationBarViewModel: BottomNavigationBarViewModel
) {
    LaunchedEffect(Unit) {
        favoriteViewModel.selectAllAlarms()
    }
    val alarms by remember { favoriteViewModel.alarms }.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isDialog = remember { mutableStateOf(false) }
    val showDatePicker = remember { mutableStateOf(false) }
    val datePickerTitle = remember { mutableStateOf("") }
    val dialogTitle = remember { mutableStateOf("") }
    val dialogText = remember { mutableStateOf("") }
    val onConfirmation = remember { mutableStateOf({}) }
    navigationBarViewModel.setCurrentWeatherTheme(currentWeather.weather.firstOrNull()?.icon ?: "")
    if (isDialog.value) {
        ConfirmationDialog(
            onConfirmation = {
                onConfirmation.value.invoke()
                isDialog.value = false
            },
            confirmText = R.string.confirm,
            onDismiss = { isDialog.value = false },
            dialogTitle = dialogTitle.value,
            dialogText = dialogText.value,
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = getWeatherGradient(
                    currentWeather.weather.firstOrNull()?.icon ?: ""
                )
            ),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 50.dp, end = 16.dp, start = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.alarms), textAlign = TextAlign.Center,
                style = Styles.textStyleSemiBold26
            )
        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp, end = 16.dp, start = 16.dp)) {
            when (alarms) {
                is Response.Failure -> item { FailureDisplay((alarms as Response.Failure).exception) }
                Response.Loading -> item { LoadingDisplay() }
                is Response.Success<*> -> {
                    val alarmsList = alarms as Response.Success<List<AlarmModel>>
                    if (alarmsList.result?.isEmpty() != false) {

                        item {
                            Box {
                                Column(
                                    modifier = Modifier
                                        .padding(top = 150.dp)
                                        .align(Alignment.Center)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    AnimatedPreloader(R.raw.alarm,Modifier.size(200.dp))
                                }
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        stringResource(R.string.the_journey_s_quiet_no_weather_alarms_on_your_path),
                                        style = Styles.textStyleSemiBold18,
                                        textAlign = TextAlign.Center,
                                        color = Color.White,
                                        modifier = Modifier
                                            .padding(top = 280.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        items(alarmsList.result?.size ?: 0) { index ->
                            val item = alarmsList.result?.get(index)
                            val selectedItem = remember {  mutableStateOf(0)}
                            if (showDatePicker.value) {
                                DateAndTimePickerForUpdate(
                                    showDatePicker = showDatePicker,
                                    title = datePickerTitle,
                                    selectedAlarm = alarmsList.result?.get(selectedItem.value),
                                    favoriteViewModel = favoriteViewModel,
                                    snackBarHostState = snackBarHostState,
                                    coroutineScope = coroutineScope
                                )
                            }
                            Box {
                                Box(
                                    modifier = Modifier
                                        .padding(bottom = 12.dp)
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .background(
                                                brush = getWeatherGradient(
                                                    currentWeather.weather.firstOrNull()?.icon ?: ""
                                                ),
                                                shape = RoundedCornerShape(25.dp)
                                            )
                                            .padding(24.dp),
                                        elevation = CardDefaults.cardElevation(),
                                        colors = CardColors(
                                            containerColor = Color.Transparent,
                                            contentColor = Color.White,
                                            disabledContainerColor = Color.Transparent,
                                            disabledContentColor = Color.White
                                        )
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.height(180.dp)
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column {
                                                    Text(
                                                        text = item?.cityName
                                                            ?: stringResource(R.string.n_a),
                                                        style = Styles.textStyleSemiBold20,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier.fillMaxWidth(0.6f)
                                                    )
                                                    Text(
                                                        text = item?.countryName
                                                            ?: stringResource(R.string.n_a),
                                                        style = Styles.textStyleNormal16
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Image(
                                                        painter = painterResource(R.drawable.notification),
                                                        contentDescription = stringResource(
                                                            R.string.notification_icon
                                                        ),
                                                        modifier = Modifier.size(25.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(5.dp))
                                                    Column(verticalArrangement = Arrangement.Center) {
                                                        Text(
                                                            convertToArabicNumbers(
                                                                item?.time
                                                                    ?: stringResource(R.string.no_alarm_set),
                                                                context
                                                            ),
                                                            fontSize = 16.sp
                                                        )
                                                        Spacer(Modifier.height(4.dp))
                                                        Text(
                                                            convertToArabicNumbers(
                                                                item?.date
                                                                    ?: stringResource(R.string.stay_ready),
                                                                context
                                                            ),
                                                            fontSize = 15.sp
                                                        )
                                                    }
                                                }
                                                Switch(
                                                    checked = true,
                                                    onCheckedChange = {
                                                        isDialog.value = true
                                                        dialogTitle.value =
                                                            context.resources.getString(R.string.warning)
                                                        dialogText.value =
                                                            context.getString(R.string.you_sure_you_want_to_reset_the_alarm)
                                                        onConfirmation.value = {
                                                            deleteAlarm(
                                                                selectedAlarm = item,
                                                                favoriteViewModel = favoriteViewModel,
                                                                context = context,
                                                                coroutineScope = coroutineScope,
                                                                snackBarHostState = snackBarHostState
                                                            )
                                                            isDialog.value = false
                                                        }

                                                    },
                                                    colors = SwitchDefaults.colors(
                                                        checkedThumbColor = PrimaryColor,
                                                        checkedTrackColor = OffWhite,
                                                        uncheckedThumbColor = PrimaryColor,
                                                        uncheckedTrackColor = OffWhite,
                                                    )
                                                )
                                            }

                                            Text(
                                                text = if (item?.alarmType == "Alert") stringResource(
                                                    R.string.alert
                                                ) else stringResource(
                                                    R.string.notification
                                                ),
                                                style = Styles.textStyleMedium18,
                                                modifier = Modifier.padding(start = 3.dp)
                                            )
                                        }

                                    }
                                }
                                Icon(
                                    painter = painterResource(R.drawable.baseline_edit_notifications_24),
                                    contentDescription = stringResource(R.string.edit_alarm_icon),
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(alignment = Alignment.TopEnd)
                                        .padding(top = 5.dp, end = 10.dp)
                                        .size(26.dp)
                                        .clickable {
                                            Log.i("TAG", "AlarmScreen: ${selectedItem.value} , $index")
                                            selectedItem.value = index
                                            showDatePicker.value = true
                                            datePickerTitle.value =
                                                item?.cityName ?: ""
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}






