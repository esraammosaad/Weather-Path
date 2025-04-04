package com.example.weatherapp.settings.view

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.home.view_model.HomeViewModelImpl
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.localization.LocalizationHelper
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.Styles
import com.example.weatherapp.utilis.getWeatherGradient
import com.example.weatherapp.utilis.view.ConfirmationDialog
import com.example.weatherapp.utilis.view.RadioButtonSingleSelection


@Composable
fun SettingsScreen(
    homeViewModel: HomeViewModelImpl,
    currentWeather: CurrentWeatherResponse,
    snackBarHostState: SnackbarHostState,
    navigationBarViewModel: BottomNavigationBarViewModel,
    onMapClicked: () -> Unit
) {

    val context = LocalContext.current
    val tempState =
        remember { mutableIntStateOf(LocalStorageDataSource.getInstance(context).getTempSymbol) }
    val windState =
        remember { mutableIntStateOf(LocalStorageDataSource.getInstance(context).getWindUnit) }
    val test = LocalStorageDataSource.getInstance(context).getLocationState

    val locationState =
        remember { mutableIntStateOf(if (test == R.string.map) R.string.map else R.string.gps) }

    val openAlertDialog = remember { mutableStateOf(false) }
    val dialogTitle = remember { mutableStateOf("") }
    val dialogText = remember { mutableStateOf("") }
    val onConfirmation = remember { mutableStateOf({}) }

    if (openAlertDialog.value) {
        ConfirmationDialog(
            onConfirmation = onConfirmation.value,
            dialogText = dialogText.value,
            dialogTitle = dialogTitle.value,
            onDismiss = {
                openAlertDialog.value = false
            },
            showRadioButton = false,
            confirmText = R.string.confirm,
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
                stringResource(R.string.preferences), textAlign = TextAlign.Center,
                style = Styles.textStyleSemiBold26
            )
        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp, end = 16.dp, start = 16.dp)) {

            item {
                val savedLangCode = LocalStorageDataSource.getInstance(context).getLanguageCode
                val selectedLanguage: String = when (savedLangCode) {
                    "en" -> context.getString(R.string.english)
                    "ar" -> context.getString(R.string.arabic)
                    else -> context.getString(R.string.default_lang)
                }
                Spacer(modifier = Modifier.height(8.dp))
                CustomPreferencesCard(
                    stringResource(R.string.language),
                    listOf(
                        stringResource(R.string.default_lang),
                        stringResource(R.string.english),
                        stringResource(R.string.arabic)

                    ),
                    R.drawable.baseline_language_24,
                    currentWeather.weather.firstOrNull()?.icon ?: "",
                    selectedLanguage
                ) { selectedLang ->
                    val langCode = when (selectedLang) {
                        context.getString(R.string.english) -> "en"
                        context.getString(R.string.arabic) -> "ar"
                        else -> getDefaultSystemLang()
                    }
                    LocalStorageDataSource.getInstance(context).saveLanguageCode(langCode)
                    LocalizationHelper.updateLocale(context, if(langCode.length>2) langCode.substring(0,2) else langCode)
                    (context as Activity).recreate()

                }
                Spacer(modifier = Modifier.height(18.dp))
                CustomPreferencesCard(
                    stringResource(R.string.get_location_by),
                    listOf(
                        stringResource(R.string.gps),
                        stringResource(R.string.map)
                    ),
                    R.drawable.baseline_location_pin_24,
                    currentWeather.weather.firstOrNull()?.icon ?: "",
                    stringResource(locationState.intValue)
                ) { selectedOption ->
                    if (selectedOption == context.getString(R.string.map)) {
                        openAlertDialog.value = true
                        dialogTitle.value = context.getString(R.string.warning)
                        dialogText.value =
                            context.getString(R.string.you_sure_you_want_change_your_location_and_pick_from_the_map)
                        onConfirmation.value = {
                            onMapClicked.invoke()
                            locationState.intValue = R.string.map
                            LocalStorageDataSource.getInstance(context).saveLocationState(0)
                            (context as Activity).recreate()

                        }
                    } else {
                        openAlertDialog.value = true
                        dialogTitle.value = context.getString(R.string.warning)
                        dialogText.value =
                            context.getString(R.string.you_sure_you_want_the_gps_determine_your_location)
                        onConfirmation.value = {
                            LocalStorageDataSource.getInstance(context).saveLocationState(R.string.gps)
                            locationState.value = R.string.gps
                            (context as Activity).recreate()
                        }
                    }


                }
                Spacer(modifier = Modifier.height(18.dp))

                CustomPreferencesCard(
                    stringResource(R.string.temperature),
                    listOf(
                        stringResource(R.string.celsius),
                        stringResource(R.string.Kelvin),
                        stringResource(R.string.fahrenheit)
                    ),
                    R.drawable.thermometer,
                    currentWeather.weather.firstOrNull()?.icon ?: "",
                    stringResource(tempState.intValue)
                ) { selectedTempUnit ->
                    val tempUnit: String
                    val tempSymbol: Int
                    when (selectedTempUnit) {
                        context.getString(R.string.celsius) -> {
                            tempUnit = Strings.CELSIUS
                            tempSymbol = R.string.celsius
                            tempState.intValue = R.string.celsius
                            windState.intValue = R.string.meter_sec
                            LocalStorageDataSource.getInstance(context).saveWindUnit(R.string.meter_sec)
                        }

                        context.getString(R.string.Kelvin) -> {
                            tempUnit = Strings.KELVIN
                            tempSymbol = R.string.Kelvin
                            tempState.intValue = R.string.Kelvin
                            windState.intValue = R.string.meter_sec
                            LocalStorageDataSource.getInstance(context).saveWindUnit(R.string.meter_sec)
                        }

                        context.getString(R.string.fahrenheit) -> {
                            tempUnit = Strings.FAHRENHEIT
                            tempSymbol = R.string.fahrenheit
                            tempState.intValue = R.string.fahrenheit
                            windState.intValue = R.string.miles_hour
                            LocalStorageDataSource.getInstance(context)
                                .saveWindUnit(R.string.miles_hour)
                        }

                        else -> {
                            tempUnit = Strings.CELSIUS
                            tempSymbol = R.string.celsius
                            tempState.intValue = R.string.celsius
                            windState.intValue = R.string.meter_sec
                            LocalStorageDataSource.getInstance(context).saveWindUnit(R.string.meter_sec)
                        }
                    }
                    saveNewTempUnit(context, tempUnit, tempSymbol, homeViewModel, currentWeather)
                }
                Spacer(modifier = Modifier.height(18.dp))
                CustomPreferencesCard(
                    stringResource(R.string.wind_speed), listOf(
                        stringResource(R.string.meter_sec),
                        stringResource(
                            R.string.miles_hour
                        )
                    ),
                    R.drawable.wind,
                    currentWeather.weather.firstOrNull()?.icon ?: "",
                    stringResource(windState.intValue)
                ) { selectedOption ->
                    if (selectedOption == context.getString(R.string.miles_hour)) {
                        LocalStorageDataSource.getInstance(context).saveWindUnit(R.string.miles_hour)
                        saveNewTempUnit(
                            context, homeViewModel = homeViewModel, currentWeather = currentWeather,
                            tempUnit = Strings.FAHRENHEIT, tempSymbol = R.string.fahrenheit
                        )
                        tempState.intValue =
                            R.string.fahrenheit
                        windState.intValue = R.string.miles_hour
                    } else {
                        LocalStorageDataSource.getInstance(context).saveWindUnit(R.string.meter_sec)
                        saveNewTempUnit(
                            context, homeViewModel = homeViewModel, currentWeather = currentWeather,
                            tempUnit = Strings.CELSIUS, tempSymbol = R.string.celsius
                        )

                        tempState.intValue =
                            R.string.celsius
                        windState.intValue = R.string.meter_sec

                    }

                }
                Spacer(modifier = Modifier.height(20.dp))

            }




        }


    }
}

fun getDefaultSystemLang() = ConfigurationCompat.getLocales(
    Resources.getSystem().configuration
).get(0).toString()

private fun saveNewTempUnit(
    context: Context,
    tempUnit: String,
    tempSymbol: Int,
    homeViewModel: HomeViewModelImpl,
    currentWeather: CurrentWeatherResponse
) {
    LocalStorageDataSource.getInstance(context).saveTempUnit(tempUnit)
    LocalStorageDataSource.getInstance(context).saveTempSymbol(tempSymbol)
    homeViewModel.getCurrentWeather(
        latitude = currentWeather.latitude,
        longitude = currentWeather.longitude,
        tempUnit = tempUnit,
        languageCode = LocalStorageDataSource.getInstance(context).getLanguageCode,
        isConnected = true
    )
    homeViewModel.getFiveDaysWeatherForecast(
        latitude = currentWeather.latitude,
        longitude = currentWeather.longitude,
        tempUnit = tempUnit,
        languageCode = LocalStorageDataSource.getInstance(context).getLanguageCode,
        isConnected = true
    )
}


@Composable
private fun CustomPreferencesCard(
    text: String,
    radioButtonList: List<String>,
    icon: Int,
    backgroundColor: String,
    defaultSelectedItem: String,
    onOptionClicked: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(
                brush = getWeatherGradient(backgroundColor),
                shape = RoundedCornerShape(25.dp)
            )
            .padding(12.dp),
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
            modifier = Modifier.height(120.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = "Icon"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = text,
                    style = Styles.textStyleSemiBold20,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.65f)
                )

            }
            Spacer(modifier = Modifier.height(18.dp))
            RadioButtonSingleSelection(onOptionClicked = { selectedOption ->
                onOptionClicked.invoke(selectedOption)
            }, radioButtonList, defaultSelectedItem)

        }
    }
}






    
