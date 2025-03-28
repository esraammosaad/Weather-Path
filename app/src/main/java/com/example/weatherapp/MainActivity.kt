package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.remote.RetrofitFactory
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.home.view_model.HomeViewModelImpl
import com.example.weatherapp.home.view_model.HomeViewModelFactory
import com.example.weatherapp.internet.InternetConnectivityViewModel
import com.example.weatherapp.internet.InternetConnectivityViewModelFactory
import com.example.weatherapp.internet.InternetObserverImpl
import com.example.weatherapp.landing.view.GetStartedScreen
import com.example.weatherapp.landing.view.SplashScreen
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.utilis.BottomNavigationBar
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.view.FailureDisplay
import com.example.weatherapp.utilis.view.LoadingDisplay
import com.example.weatherapp.utilis.NavHostImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


private const val My_LOCATION_PERMISSION_ID = 5005

class MainActivity : ComponentActivity() {
    lateinit var locationState: MutableState<Location>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var bottomNavigationBarViewModel: BottomNavigationBarViewModel
    private lateinit var homeViewModel: HomeViewModelImpl
    var isConnected  = mutableStateOf(true)
    override fun onCreate(savedInstanceState: Bundle?) {

        locationState =
            mutableStateOf(Location(LocationManager.GPS_PROVIDER))
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(
                Repository.getInstance(
                    weatherRemoteDataSource = WeatherRemoteDataSource(RetrofitFactory.apiService),
                    weatherLocalDataSource = WeatherLocalDataSource(
                        WeatherDatabase.getInstance(
                            this
                        ).getDao()
                    )
                )
            )
        )[HomeViewModelImpl::class]
        enableEdgeToEdge()
        val internetConnectivityViewModel = ViewModelProvider(
            this@MainActivity, InternetConnectivityViewModelFactory(
                InternetObserverImpl(
                    this@MainActivity
                )
            )
        )[InternetConnectivityViewModel::class]


        setContent {
            val isSeenGetStartedScreen =
                remember { mutableStateOf(LocalStorageDataSource.getInstance(this).getStartedState) }
            val isSplash = remember { mutableStateOf(true) }
            bottomNavigationBarViewModel = BottomNavigationBarViewModel()
            val navController = rememberNavController()
            val currentWeather by homeViewModel.currentWeather.collectAsStateWithLifecycle()
            val fiveDaysWeatherForecast by
            homeViewModel.fiveDaysWeatherForecast.collectAsStateWithLifecycle()
            val countryName by homeViewModel.countryName.collectAsStateWithLifecycle()
            val openAlertDialog = remember { mutableStateOf(false) }
            val dialogTitle = remember { mutableStateOf("") }
            val dialogText = remember { mutableStateOf("") }
            val onConfirmation = remember { mutableStateOf({}) }
            val showRadioButton = remember { mutableStateOf(false) }
            val option = remember { mutableStateOf("") }
            isConnected.value= internetConnectivityViewModel.isConnected.collectAsStateWithLifecycle().value
            OpenAlertDialog(
                openAlertDialog,
                onConfirmation,
                dialogText,
                dialogTitle,
                showRadioButton,
                option
            )
            if (isSplash.value) {
                SplashScreen {
                    isSplash.value = false
                }
            } else {
                if (!isSeenGetStartedScreen.value) {
                    AnimatedVisibility(visible = !isSeenGetStartedScreen.value) {
                        GetStartedScreen(
                            onAllowPermissionClicked = {
                                onAllowPermissionButtonClicked(
                                    showRadioButton,
                                    openAlertDialog,
                                    dialogTitle,
                                    dialogText,
                                    onConfirmation
                                )
                            },
                            onGetStartedClicked = {
                                showRadioButton.value = false
                                openAlertDialog.value = true
                                if (checkLocationPermission()) {
                                    if (isLocationEnabled()) {
                                        dialogTitle.value = getString(R.string.choose)
                                        dialogText.value =
                                            getString(R.string.chooseMapOrGPS)
                                        showRadioButton.value = true
                                        onConfirmation.value = {
                                            if (option.value == "GPS") {
                                                getLocation()

                                            } else {
                                                //map
                                            }
                                            LocalStorageDataSource.getInstance(this@MainActivity)
                                                .saveLocationState(option.value)
                                            isSeenGetStartedScreen.value = true
                                            LocalStorageDataSource.getInstance(this@MainActivity)
                                                .saveGetStartedStateState()
                                            openAlertDialog.value = false
                                        }
                                    } else {
                                        allowPermissionDialog(
                                            dialogTitle,
                                            dialogText,
                                            onConfirmation,
                                            openAlertDialog,

                                            )
                                    }
                                } else {
                                    allowPermissionDialog(
                                        dialogTitle,
                                        dialogText,
                                        onConfirmation,
                                        openAlertDialog,
                                    )
                                }
                            }

                        )
                    }

                } else {
                    when (currentWeather) {
                        is Response.Loading -> LoadingDisplay()
                        is Response.Success<*> -> {
                            currentWeather as Response.Success<CurrentWeatherResponse>
                            bottomNavigationBarViewModel.setCurrentWeatherTheme(
                                (currentWeather as Response.Success<CurrentWeatherResponse>).result?.weather?.get(
                                    0
                                )?.icon ?: ""
                            )
                            val snackBarHostState = remember { SnackbarHostState() }
                            AnimatedVisibility(visible = isSeenGetStartedScreen.value) {
                                MainScreen(
                                    snackBarHostState,
                                    navController,
                                    currentWeather,
                                    countryName,
                                    fiveDaysWeatherForecast
                                )
                            }
                        }

                        is Response.Failure -> FailureDisplay((currentWeather as Response.Failure).exception)

                    }
                }


            }


        }
    }

    @Composable
    private fun OpenAlertDialog(
        openAlertDialog: MutableState<Boolean>,
        onConfirmation: MutableState<() -> Unit>,
        dialogText: MutableState<String>,
        dialogTitle: MutableState<String>,
        showRadioButton: MutableState<Boolean>,
        option: MutableState<String>
    ) {
        if (openAlertDialog.value) {
            AlertDialogExample(
                onConfirmation = onConfirmation.value,
                dialogText = dialogText.value,
                dialogTitle = dialogTitle.value,
                onDismiss = {
                    openAlertDialog.value = false
                },
                showRadioButton = showRadioButton.value,
                onOptionClicked = { text ->
                    option.value = text
                }
            )
        }
    }

    private fun onAllowPermissionButtonClicked(
        showRadioButton: MutableState<Boolean>,
        openAlertDialog: MutableState<Boolean>,
        dialogTitle: MutableState<String>,
        dialogText: MutableState<String>,
        onConfirmation: MutableState<() -> Unit>
    ) {
        showRadioButton.value = false
        if (checkLocationPermission()) {
            openAlertDialog.value = true
            if (isLocationEnabled()) {
                dialogTitle.value = getString(R.string.confirmation)
                dialogText.value =
                    getString(R.string.your_location_already_enabled_let_s_get_started)
                onConfirmation.value = { openAlertDialog.value = false }
            } else {
                dialogTitle.value = getString(R.string.warning)
                dialogText.value =
                    getString(R.string.you_need_enable_your_location_go_to_the_settings)
                onConfirmation.value = {
                    enableLocationServices()
                    openAlertDialog.value = false
                }
            }
        } else {
            requestPermission()
        }
    }

    private fun allowPermissionDialog(
        dialogTitle: MutableState<String>,
        dialogText: MutableState<String>,
        onConfirmation: MutableState<() -> Unit>,
        openAlertDialog: MutableState<Boolean>,
    ) {
        dialogTitle.value = getString(R.string.warning)
        dialogText.value =
            "Please allow permission first"
        onConfirmation.value = {
            openAlertDialog.value = false
        }

    }

    @Composable
    private fun MainScreen(
        snackBarHostState: SnackbarHostState,
        navController: NavHostController,
        currentWeather: Response,
        countryName: Response,
        fiveDaysWeatherForecast: Response
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            contentWindowInsets = WindowInsets(0.dp),
            bottomBar = {
                BottomNavigationBar(
                    navController,
                    bottomNavigationBarViewModel
                )

            },
            modifier = Modifier
                .fillMaxSize(),
        ) { innerPadding ->
            (currentWeather as Response.Success<CurrentWeatherResponse>).result.let {
                when (countryName) {
                    is Response.Success<*> -> {
                        countryName as Response.Success<Address>
                        when (fiveDaysWeatherForecast) {
                            is Response.Failure -> {}
                            Response.Loading -> {}
                            is Response.Success<*> -> {
                                fiveDaysWeatherForecast as Response.Success<List<WeatherItem>>
                                homeViewModel.insertCurrentWeather(
                                    currentWeather.result,
                                    countryName.result,
                                    locationState.value
                                )
                                homeViewModel.insertFiveDaysForecast(
                                    fiveDaysWeatherForecast.result,
                                    locationState.value
                                )
                            }
                        }
                        if (it != null) {
                            NavHostImpl(
                                navController,
                                it,
                                countryName.result,
                                innerPadding,
                                snackBarHostState,
                                homeViewModel,
                                bottomNavigationBarViewModel,
                                locationState

                            )
                        }
                    }

                    is Response.Failure -> FailureDisplay(countryName.exception)
                    Response.Loading -> LoadingDisplay()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (LocalStorageDataSource.getInstance(this).getLocationState == "GPS") {
            getLocation()
        }
    }


    private fun enableLocationServices() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == My_LOCATION_PERMISSION_ID) {
            if (grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            My_LOCATION_PERMISSION_ID
        )

    }

    private fun checkLocationPermission(): Boolean {
        return !(ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        val locationRequest = LocationRequest.Builder(3600000).apply {
            setPriority(Priority.PRIORITY_LOW_POWER)
            setMinUpdateIntervalMillis(3600000)
        }.build()
        val locationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                locationState.value =
                    locationResult.lastLocation ?: Location(LocationManager.GPS_PROVIDER)
                homeViewModel.getWeatherFromApi(locationState.value, Geocoder(this@MainActivity),isConnected.value)

            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.myLooper()
        )
    }

}

@Composable
fun AlertDialogExample(
    onConfirmation: () -> Unit,
    onDismiss: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    showRadioButton: Boolean,
    onOptionClicked: (String) -> Unit
) {
    AlertDialog(
        icon = {
            Icon(
                painterResource(R.drawable.baseline_location_pin_24),
                contentDescription = "Confirmation Icon",
                tint = PrimaryColor
            )
        },
        title = {
            Text(text = dialogTitle, textAlign = TextAlign.Center)
        },
        text = {
            Column {
                Text(text = dialogText, textAlign = TextAlign.Center)
                if (showRadioButton) {
                    Spacer(modifier = Modifier.height(8.dp))
                    RadioButtonSingleSelection(onOptionClicked)
                }
            }
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm", color = PrimaryColor)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Dismiss", color = PrimaryColor)
            }
        }

    )
}

@Composable
fun RadioButtonSingleSelection(onOptionClicked: (String) -> Unit) {
    val radioOptions = listOf("Map", "GPS")
    val (selectedOption, optionClicked) = remember { mutableStateOf(radioOptions[0]) }
    Column(Modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            optionClicked(text)
                            onOptionClicked(text)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}