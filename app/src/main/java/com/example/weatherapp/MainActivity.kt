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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.example.weatherapp.utilis.internet.InternetConnectivityViewModel
import com.example.weatherapp.utilis.internet.InternetConnectivityViewModelFactory
import com.example.weatherapp.utilis.internet.InternetObserverImpl
import com.example.weatherapp.landing.view.GetStartedScreen
import com.example.weatherapp.landing.view.SplashScreen
import com.example.weatherapp.utilis.BottomNavigationBar
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.view.FailureDisplay
import com.example.weatherapp.utilis.view.LoadingDisplay
import com.example.weatherapp.utilis.NavHostImpl
import com.example.weatherapp.utilis.view.ConfirmationDialog
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
    private lateinit var internetConnectivityViewModel: InternetConnectivityViewModel
    var isConnected = mutableStateOf(true)
    override fun onCreate(savedInstanceState: Bundle?) {
        locationState =
            mutableStateOf(Location(LocationManager.GPS_PROVIDER))
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)
        homeViewModel = homeViewModel()
        internetConnectivityViewModel = internetConnectivityViewModel()
        bottomNavigationBarViewModel = BottomNavigationBarViewModel()
        enableEdgeToEdge()
        setContent {
            val isSeenGetStartedScreen =
                remember { mutableStateOf(LocalStorageDataSource.getInstance(this).getStartedState) }
            val isSplash = remember { mutableStateOf(true) }
            val navController = rememberNavController()
            val currentWeather by homeViewModel.currentWeather.collectAsStateWithLifecycle()
            val fiveDaysWeatherForecast by homeViewModel.fiveDaysWeatherForecast.collectAsStateWithLifecycle()
            val countryName by homeViewModel.countryName.collectAsStateWithLifecycle()
            internetConnectivityViewModel.getInternetConnectivity()
            isConnected.value =
                internetConnectivityViewModel.isConnected.collectAsStateWithLifecycle().value
            if (LocalStorageDataSource.getInstance(this).getLocationState == stringResource(R.string.gps)) {
                getLocation()
            }
            val openAlertDialog = remember { mutableStateOf(false) }
            val dialogTitle = remember { mutableStateOf("") }
            val dialogText = remember { mutableStateOf("") }
            val onConfirmation = remember { mutableStateOf({}) }
            val showRadioButton = remember { mutableStateOf(false) }
            val option = remember { mutableStateOf("") }
            if (openAlertDialog.value) {
                ConfirmationDialog(
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
            if (isSplash.value) {
                SplashScreen {
                    isSplash.value = false
                }
            } else {
                if (!isSeenGetStartedScreen.value) {
                    GetStartedContent(
                        isSeenGetStartedScreen,
                        showRadioButton,
                        openAlertDialog,
                        dialogTitle,
                        dialogText,
                        onConfirmation,
                        option
                    )
                } else {
                    val snackBarHostState = remember { SnackbarHostState() }
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
                        MainScreen(
                            currentWeather,
                            isSeenGetStartedScreen,
                            countryName,
                            fiveDaysWeatherForecast,
                            navController,
                            innerPadding,
                            snackBarHostState
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun MainScreen(
        currentWeather: Response,
        isSeenGetStartedScreen: MutableState<Boolean>,
        countryName: Response,
        fiveDaysWeatherForecast: Response,
        navController: NavHostController,
        innerPadding: PaddingValues,
        snackBarHostState: SnackbarHostState
    ) {
        when (currentWeather) {
            is Response.Loading -> LoadingDisplay()
            is Response.Success<*> -> {
                val currentWeatherResponse =
                    currentWeather as Response.Success<CurrentWeatherResponse>
                bottomNavigationBarViewModel.setCurrentWeatherTheme(
                    currentWeatherResponse.result?.weather?.firstOrNull()?.icon
                        ?: ""
                )
                AnimatedVisibility(visible = isSeenGetStartedScreen.value) {
                    currentWeatherResponse.result.let {
                        when (countryName) {
                            is Response.Success<*> -> {
                                val cName = countryName as Response.Success<Address>
                                when (fiveDaysWeatherForecast) {
                                    is Response.Failure -> {}
                                    Response.Loading -> {}
                                    is Response.Success<*> -> {
                                        val fiveDaysWeatherForecastResponse =
                                            fiveDaysWeatherForecast as Response.Success<List<WeatherItem>>
                                        homeViewModel.insertCurrentWeather(
                                            it,
                                            cName.result,
                                            locationState.value
                                        )
                                        homeViewModel.insertFiveDaysForecast(
                                            fiveDaysWeatherForecastResponse.result,
                                            locationState.value
                                        )
                                    }
                                }
                                if (it != null) {
                                    NavHostImpl(
                                        navController = navController,
                                        currentWeatherResponse = it,
                                        countryName = cName.result,
                                        innerPadding = innerPadding,
                                        snackBarHostState = snackBarHostState,
                                        homeViewModel = homeViewModel,
                                        bottomNavigationBarViewModel = bottomNavigationBarViewModel,
                                        locationState = locationState,

                                        )
                                }
                            }

                            is Response.Failure -> FailureDisplay(countryName.exception)
                            Response.Loading -> LoadingDisplay()
                        }
                    }


                }
            }

            is Response.Failure -> FailureDisplay(currentWeather.exception)
        }
    }
    @Composable
    private fun GetStartedContent(
        isSeenGetStartedScreen: MutableState<Boolean>,
        showRadioButton: MutableState<Boolean>,
        openAlertDialog: MutableState<Boolean>,
        dialogTitle: MutableState<String>,
        dialogText: MutableState<String>,
        onConfirmation: MutableState<() -> Unit>,
        option: MutableState<String>
    ) {
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
                                    if (isConnected.value)
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
    }

    private fun homeViewModel() = ViewModelProvider(
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
    private fun internetConnectivityViewModel() = ViewModelProvider(
        this@MainActivity, InternetConnectivityViewModelFactory(
            InternetObserverImpl(
                this@MainActivity
            )
        )
    )[InternetConnectivityViewModel::class]
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
            getString(R.string.please_allow_permission_first)
        onConfirmation.value = {
            openAlertDialog.value = false
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
                    homeViewModel.getWeatherFromApi(
                        locationState.value,
                        Geocoder(this@MainActivity),
                        isConnected.value
                    )


            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.myLooper()
        )
    }

}



