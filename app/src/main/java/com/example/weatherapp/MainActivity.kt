package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitFactory
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteViewModelFactory
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteAndAlarmSharedViewModelImpl
import com.example.weatherapp.home_settings_feature.LocationPickScreen
import com.example.weatherapp.home_settings_feature.view_model.HomeViewModelFactory
import com.example.weatherapp.home_settings_feature.view_model.HomeAndSettingsSharedViewModelImpl
import com.example.weatherapp.landing.view.GetStartedScreen
import com.example.weatherapp.landing.view.SplashScreen
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.utilis.view.BottomNavigationBar
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.landing.view.MainScreen
import com.example.weatherapp.utilis.Strings
import com.example.weatherapp.utilis.internet.InternetConnectivityViewModel
import com.example.weatherapp.utilis.internet.InternetConnectivityViewModelFactory
import com.example.weatherapp.utilis.internet.InternetObserverImpl
import com.example.weatherapp.utilis.localization.LocalizationHelper
import com.example.weatherapp.utilis.view.ConfirmationDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import java.util.Locale


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var bottomNavigationBarViewModel: BottomNavigationBarViewModel
    private lateinit var homeViewModel: HomeAndSettingsSharedViewModelImpl
    private lateinit var internetConnectivityViewModel: InternetConnectivityViewModel
    var isConnected = mutableStateOf(false)
    lateinit var locationState: MutableState<Location>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)
        homeViewModel = homeViewModel()
        internetConnectivityViewModel = internetConnectivityViewModel()
        bottomNavigationBarViewModel = BottomNavigationBarViewModel()
        val langCode = LocalStorageDataSource.getInstance(this).getLanguageCode
        LocalizationHelper.updateLocale(
            this,
            if (langCode.length > 2) ConfigurationCompat.getLocales(Resources.getSystem().configuration)
                .get(0).toString().substring(0, 2) else langCode
        )
        enableEdgeToEdge()
        setContent {
            locationState =
                rememberSaveable { mutableStateOf(Location(LocationManager.GPS_PROVIDER)) }
//            val systemUiController: SystemUiController = rememberSystemUiController()
//            systemUiController.isStatusBarVisible = false // Status bar
//            systemUiController.isNavigationBarVisible = false // Navigation bar
//            systemUiController.isSystemBarsVisible = false // Status & Navigation bars
            val isSeenGetStartedScreen =
                remember { mutableStateOf(LocalStorageDataSource.getInstance(this).getStartedState) }
            val isSplash = rememberSaveable { mutableStateOf(true) }
            val isLocationPickScreen = remember { mutableStateOf(false) }
            val navController = rememberNavController()
            val currentWeather by homeViewModel.currentWeather.collectAsStateWithLifecycle()
            val fiveDaysWeatherForecast by homeViewModel.fiveDaysWeatherForecast.collectAsStateWithLifecycle()
            val countryName by homeViewModel.countryName.collectAsStateWithLifecycle()
            internetConnectivityViewModel.getInternetConnectivity()
            isConnected.value =
                internetConnectivityViewModel.isConnected.collectAsStateWithLifecycle().value
            val openAlertDialog = remember { mutableStateOf(false) }
            val dialogTitle = remember { mutableStateOf("") }
            val dialogText = remember { mutableStateOf("") }
            val confirmText = remember { mutableIntStateOf(R.string.confirm) }
            val onConfirmation = remember { mutableStateOf({}) }
            val showRadioButton = remember { mutableStateOf(false) }
            val option = remember { mutableStateOf("") }
            val radioButtonState = remember { mutableStateOf(getString(R.string.map)) }
            LaunchedEffect(isConnected.value) {
                if (LocalStorageDataSource.getInstance(this@MainActivity).getLocationState == R.string.gps) {
                    getLocation()
                } else {
                    loadPickedLocationFromMap()
                }
                delay(1000)
                checkInternet(
                    openAlertDialog,
                    dialogTitle,
                    dialogText,
                    confirmText,
                    onConfirmation,
                    showRadioButton
                )
            }
            if (openAlertDialog.value) {
                ConfirmationDialog(
                    onConfirmation = onConfirmation.value,
                    dialogText = dialogText.value,
                    dialogTitle = dialogTitle.value,
                    onDismiss = {
                        openAlertDialog.value = false
                    },
                    showRadioButton = showRadioButton.value,
                    radioButtonState = radioButtonState,
                    confirmText = confirmText.intValue,
                    onOptionClicked = { text ->
                        radioButtonState.value = text
                        option.value = text
                    }
                )
            }
            if (isSplash.value) {
                SplashScreen {
                    isSplash.value = false
                }
            } else if (isLocationPickScreen.value) {
                LocationPickScreen(
                    onBackClicked = {
                        isSeenGetStartedScreen.value = false
                        isLocationPickScreen.value = false
                    },
                    homeViewModel = homeViewModel,
                    location = locationState,
                    isConnected = isConnected.value,
                    bottomNavigationBarViewModel = bottomNavigationBarViewModel,
                ) {
                    isLocationPickScreen.value = false
                    isSeenGetStartedScreen.value = true
                    LocalStorageDataSource.getInstance(this@MainActivity)
                        .saveGetStartedStateState()
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
                        option,
                        isLocationPickScreen,
                        confirmText
                    )
                } else {
                    val snackBarHostState = remember { SnackbarHostState() }
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(snackBarHostState) { data ->
                                Snackbar(
                                    actionColor = PrimaryColor,
                                    snackbarData = data,
                                    containerColor = OffWhite,
                                    contentColor = Color.Black,
                                    dismissActionContentColor = Color.Black
                                )
                            }
                        },
                        contentWindowInsets = WindowInsets(0.dp),
                        bottomBar = {
                            BottomNavigationBar(
                                navController = navController,
                                bottomNavigationBarViewModel = bottomNavigationBarViewModel,
                                favoriteViewModel = getFavoriteViewModel()
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
                            snackBarHostState,
                            locationState,
                            isConnected,
                            homeViewModel,
                            bottomNavigationBarViewModel
                        )
                    }
                }
            }
        }
    }

    private fun getFavoriteViewModel() = ViewModelProvider(
        this,
        FavoriteViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                weatherRemoteDataSourceImpl = WeatherRemoteDataSourceImpl(
                    RetrofitFactory.apiService
                ),
                weatherLocalDataSourceImpl = WeatherLocalDataSourceImpl(
                    WeatherDatabase.getInstance(
                        this
                    ).getDao()
                )
            )
        )
    )[FavoriteAndAlarmSharedViewModelImpl::class]

    private fun checkInternet(
        openAlertDialog: MutableState<Boolean>,
        dialogTitle: MutableState<String>,
        dialogText: MutableState<String>,
        confirmText: MutableState<Int>,
        onConfirmation: MutableState<() -> Unit>,
        showRadioButton: MutableState<Boolean>
    ) {
        if (!isConnected.value) {
            openAlertDialog.value = true
            dialogTitle.value = this.getString(R.string.warning)
            dialogText.value =
                getString(R.string.no_internet_connection_check_your_network_settings)
            showRadioButton.value = false
            confirmText.value = R.string.network_settings
            onConfirmation.value = {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }

        } else if (isConnected.value) {
            openAlertDialog.value = false
        }
    }

    private fun loadPickedLocationFromMap() {
        if (LocalStorageDataSource.getInstance(this@MainActivity).getStartedState) {
            val lat =
                LocalStorageDataSource.getInstance(this@MainActivity).getPickedLat
            val long =
                LocalStorageDataSource.getInstance(this@MainActivity).getPickedLong
            val languageCode =
                LocalStorageDataSource.getInstance(this@MainActivity).getLanguageCode
            val tempUnit =
                LocalStorageDataSource.getInstance(this@MainActivity).getTempUnit
            homeViewModel.getCurrentWeather(
                longitude = long,
                latitude = lat,
                isConnected = isConnected.value,
                languageCode = languageCode,
                tempUnit = tempUnit
            )
            homeViewModel.getFiveDaysWeatherForecast(
                longitude = long,
                latitude = lat,
                isConnected = isConnected.value,
                languageCode = languageCode,
                tempUnit = tempUnit
            )
            homeViewModel.getCountryName(
                longitude = long,
                latitude = lat,
                isConnected = isConnected.value,
                geocoder = Geocoder(
                    this@MainActivity,
                    Locale(
                        if (languageCode.length > 2) languageCode.substring(
                            0,
                            2
                        ) else languageCode
                    )
                ),
            )

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
        option: MutableState<String>,
        isLocationPickScreen: MutableState<Boolean>,
        confirmText: MutableState<Int>
    ) {
        AnimatedVisibility(visible = !isSeenGetStartedScreen.value) {
            GetStartedScreen(
                onAllowPermissionClicked = {
                    onAllowPermissionButtonClicked(
                        showRadioButton,
                        openAlertDialog,
                        dialogTitle,
                        dialogText,
                        onConfirmation,
                        confirmText
                    )
                },
                onGetStartedClicked = {
                    showRadioButton.value = false
                    openAlertDialog.value = true
                    if (checkLocationPermission()) {
                        if (isLocationEnabled() && isConnected.value) {
                            confirmText.value = R.string.confirm
                            dialogTitle.value = getString(R.string.choose)
                            dialogText.value =
                                getString(R.string.chooseMapOrGPS)
                            showRadioButton.value = true
                            onConfirmation.value = {
                                if (option.value == this@MainActivity.getString(R.string.gps)) {
                                    isSeenGetStartedScreen.value = true
                                    LocalStorageDataSource.getInstance(this@MainActivity)
                                        .saveGetStartedStateState()
                                    LocalStorageDataSource.getInstance(this@MainActivity)
                                        .saveLocationState(R.string.gps)
                                } else {
                                    isLocationPickScreen.value = true
                                    LocalStorageDataSource.getInstance(this@MainActivity)
                                        .saveLocationState(R.string.map)
                                }
                                getLocation()
                                openAlertDialog.value = false
                            }
                        } else {
                            if (isConnected.value)
                                allowPermissionDialog(
                                    dialogTitle,
                                    dialogText,
                                    onConfirmation,
                                    openAlertDialog,
                                )
                        }
                    } else {
                        if (isConnected.value)
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


    private fun askForNotificationPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            if (!notificationManager.areNotificationsEnabled()) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    10
                )
            }
        }
    }


    private fun homeViewModel() = ViewModelProvider(
        this,
        HomeViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                weatherRemoteDataSourceImpl = WeatherRemoteDataSourceImpl(RetrofitFactory.apiService),
                weatherLocalDataSourceImpl = WeatherLocalDataSourceImpl(
                    WeatherDatabase.getInstance(
                        this
                    ).getDao()
                )
            )
        )
    )[HomeAndSettingsSharedViewModelImpl::class]

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
        onConfirmation: MutableState<() -> Unit>,
        confirmText: MutableState<Int>
    ) {
        showRadioButton.value = false
        if (checkLocationPermission()) {
            openAlertDialog.value = true
            if (isConnected.value) {
                confirmText.value = R.string.confirm
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
            }
        } else {
            if (isConnected.value)
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
        if (requestCode == Strings.My_LOCATION_PERMISSION_ID) {
            if (grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        askForNotificationPermission(this)

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            Strings.My_LOCATION_PERMISSION_ID
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
        val locationRequest = LocationRequest.Builder(360000).apply {
            setPriority(Priority.PRIORITY_LOW_POWER)
            setMinUpdateIntervalMillis(360000)
        }.build()
        val locationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationState.value =
                    locationResult.lastLocation ?: Location(LocationManager.GPS_PROVIDER)
                val langCode = LocalStorageDataSource.getInstance(this@MainActivity).getLanguageCode
                val geocoder =
                    Geocoder(
                        this@MainActivity,
                        Locale(if (langCode.length > 2) langCode.substring(0, 2) else langCode)
                    )
                homeViewModel.getWeatherFromApi(
                    locationState.value,
                    geocoder,
                    isConnected.value,
                    LocalStorageDataSource.getInstance(this@MainActivity).getLanguageCode,
                    LocalStorageDataSource.getInstance(this@MainActivity).getTempUnit
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



