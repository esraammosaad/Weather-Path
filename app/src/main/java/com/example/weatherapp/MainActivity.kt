package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSourceImpl
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.remote.RetrofitFactory
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.favorite.view_model.FavoriteViewModelFactory
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.example.weatherapp.home.view.LocationPickScreen
import com.example.weatherapp.home.view_model.HomeViewModelFactory
import com.example.weatherapp.home.view_model.HomeViewModelImpl
import com.example.weatherapp.landing.view.GetStartedScreen
import com.example.weatherapp.landing.view.SplashScreen
import com.example.weatherapp.ui.theme.OffWhite
import com.example.weatherapp.ui.theme.PrimaryColor
import com.example.weatherapp.utilis.BottomNavigationBar
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.NavHostImpl
import com.example.weatherapp.utilis.internet.InternetConnectivityViewModel
import com.example.weatherapp.utilis.internet.InternetConnectivityViewModelFactory
import com.example.weatherapp.utilis.internet.InternetObserverImpl
import com.example.weatherapp.utilis.localization.LocalizationHelper
import com.example.weatherapp.utilis.view.ConfirmationDialog
import com.example.weatherapp.utilis.view.FailureDisplay
import com.example.weatherapp.utilis.view.LoadingDisplay
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import java.util.Locale


private const val My_LOCATION_PERMISSION_ID = 5005

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var bottomNavigationBarViewModel: BottomNavigationBarViewModel
    private lateinit var homeViewModel: HomeViewModelImpl
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
                    Log.i("TAG", "onCreate: helooooooo")
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
                            locationState
                        )
                    }
                }
            }
        }
    }

    @Composable
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
    )[FavoriteViewModelImpl::class]

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

            Log.i("TAG", "loadPickedLocationFromMap: $lat $long")
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


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun MainScreen(
        currentWeather: Response,
        isSeenGetStartedScreen: MutableState<Boolean>,
        countryName: Response,
        fiveDaysWeatherForecast: Response,
        navController: NavHostController,
        innerPadding: PaddingValues,
        snackBarHostState: SnackbarHostState,
        locationState: MutableState<Location>
    ) {
        val context = LocalContext.current
        CheckInternet(snackBarHostState, context)
        when (currentWeather) {
            is Response.Loading -> LoadingDisplay()
            is Response.Success<*> -> {
                val currentWeatherResponse =
                    currentWeather as Response.Success<CurrentWeatherResponse>
                if (LocalStorageDataSource.getInstance(context).getLocationState == R.string.map) {
                    currentWeather.result?.longitude =
                        LocalStorageDataSource.getInstance(context).getPickedLong
                    currentWeather.result?.latitude =
                        LocalStorageDataSource.getInstance(context).getPickedLat
                }
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
                                        insertCurrentLocation(
                                            it,
                                            cName,
                                            fiveDaysWeatherForecastResponse,
                                            locationState
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
                                        isConnected = isConnected.value,

                                        )
                                }
                            }

                            is Response.Failure -> FailureDisplay(
                                if (isConnected.value)
                                    countryName.exception
                                else stringResource(R.string.no_internet_connection)
                            )

                            Response.Loading -> LoadingDisplay()
                        }
                    }


                }
            }

            is Response.Failure -> FailureDisplay(currentWeather.exception)
        }
    }

    private fun insertCurrentLocation(
        it: CurrentWeatherResponse?,
        cName: Response.Success<Address>,
        fiveDaysWeatherForecastResponse: Response.Success<List<WeatherItem>>,
        locationState: MutableState<Location>
    ) {
        if (LocalStorageDataSource.getInstance(this@MainActivity).getLocationState == R.string.gps) {
            if (locationState.value.latitude != 0.0 && locationState.value.longitude != 0.0) {
                homeViewModel.insertCurrentWeather(
                    currentWeather = it,
                    latitude = locationState.value.latitude,
                    longitude = locationState.value.longitude,
                    countryName = cName.result,
                )
                homeViewModel.insertFiveDaysForecast(
                    fiveDaysWeatherForecast = fiveDaysWeatherForecastResponse.result,
                    latitude = locationState.value.latitude,
                    longitude = locationState.value.longitude

                )
            }
        }
    }

    @Composable
    private fun CheckInternet(
        snackBarHostState: SnackbarHostState,
        context: Context
    ) {
        LaunchedEffect(isConnected.value) {
            if (!isConnected.value) {
                val res = snackBarHostState.showSnackbar(
                    context.getString(R.string.no_internet_connection),
                    actionLabel = context.getString(R.string.network_settings),
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                when (res) {
                    SnackbarResult.ActionPerformed -> {
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));
                    }

                    SnackbarResult.Dismissed -> {
                    }
                }

            }
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
                                if (isConnected.value)
                                    getLocation()
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

                Log.i("TAG", "onLocationResult: ${locationState.value.longitude}")
                Log.i("TAG", "onLocationResult: ${locationState.value.latitude}")
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.myLooper()
        )
    }

}



