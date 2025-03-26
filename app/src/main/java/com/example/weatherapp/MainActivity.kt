package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.weatherapp.alarm.view.AlarmScreen
import com.example.weatherapp.data.local.LocalStorageDataSource
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.remote.RetrofitFactory
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.favorite.view.FavoriteScreen
import com.example.weatherapp.home.view.HomeScreen
import com.example.weatherapp.home.view_model.HomeViewModelImpl
import com.example.weatherapp.home.view_model.HomeViewModelFactory
import com.example.weatherapp.landing.view.GetStartedScreen
import com.example.weatherapp.favorite.view.MapScreen
import com.example.weatherapp.favorite.view.WeatherDetailsScreen
import com.example.weatherapp.favorite.view_model.FavoriteViewModelFactory
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.example.weatherapp.settings.view.SettingsScreen
import com.example.weatherapp.utilis.BottomNavigationBar
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.NavigationRoutes
import com.example.weatherapp.utilis.view.FailureDisplay
import com.example.weatherapp.utilis.view.LoadingDisplay
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.math.RoundingMode


private const val My_LOCATION_PERMISSION_ID = 5005

class MainActivity : ComponentActivity() {
    lateinit var locationState: MutableState<Location>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var geocoder: Geocoder
    private lateinit var bottomNavigationBarViewModel: BottomNavigationBarViewModel

    private lateinit var homeViewModel: HomeViewModelImpl
    override fun onCreate(savedInstanceState: Bundle?) {
        locationState =
            mutableStateOf(Location(LocationManager.GPS_PROVIDER))
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            homeViewModel = viewModel<HomeViewModelImpl>(
                factory = HomeViewModelFactory(
                    Repository.getInstance(
                        weatherRemoteDataSource = WeatherRemoteDataSource(RetrofitFactory.apiService),
                        weatherLocalDataSource = WeatherLocalDataSource(
                            WeatherDatabase.getInstance(
                                this
                            ).getDao()
                        )
                    )
                )
            )
            bottomNavigationBarViewModel = BottomNavigationBarViewModel()
            val navController = rememberNavController()
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry.value?.destination
            val currentWeather = homeViewModel.currentWeather.collectAsStateWithLifecycle().value
            val fiveDaysWeatherForecast =
                homeViewModel.fiveDaysWeatherForecast.collectAsStateWithLifecycle().value
            val countryName = homeViewModel.countryName.collectAsStateWithLifecycle().value

            when (currentWeather) {
                is Response.Loading -> LoadingDisplay()
                is Response.Success<*> -> {
                    currentWeather as Response.Success<CurrentWeatherResponse>
                    bottomNavigationBarViewModel.setCurrentWeatherTheme(
                        currentWeather.result?.weather?.get(
                            0
                        )?.icon ?: ""
                    )

                    Scaffold(
                        contentWindowInsets = WindowInsets(0.dp),
                        bottomBar = {
                            if (NavigationRoutes.GetStartedScreen::class.simpleName?.let {
                                    currentDestination?.route?.contains(
                                        it
                                    )
                                } == true) {
                                Unit
                            } else BottomNavigationBar(
                                navController,
                                bottomNavigationBarViewModel
                            )

                        },
                        modifier = Modifier
                            .fillMaxSize(),
                    ) { innerPadding ->
                        currentWeather.result.let {
                            when (countryName) {
                                is Response.Success<*> -> {
                                    countryName as Response.Success<Address>
                                    when (fiveDaysWeatherForecast) {
                                        is Response.Failure -> {}
                                        Response.Loading -> {}
                                        is Response.Success<*> -> {
                                            fiveDaysWeatherForecast as Response.Success<List<WeatherItem>>
                                            insertCurrentWeather(
                                                currentWeather.result,
                                                countryName.result
                                            )
                                            insertFiveDaysForecast(fiveDaysWeatherForecast.result)
                                        }
                                    }
                                    if (it != null) {
                                        NavHostImpl(
                                            navController,
                                            it,
                                            countryName.result,
                                            innerPadding,

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
    }

    private fun insertFiveDaysForecast(fiveDaysWeatherForecast: List<WeatherItem>?) {
        fiveDaysWeatherForecast?.let { it1 ->
            FiveDaysWeatherForecastResponse(
                list = it1,
                latitude = locationState.value.latitude.toBigDecimal()
                    .setScale(2, RoundingMode.DOWN).toDouble(),
                longitude = locationState.value.longitude.toBigDecimal()
                    .setScale(2, RoundingMode.DOWN).toDouble()
            )
        }?.let { it2 ->
            homeViewModel.insertFiveDaysWeather(
                it2

            )
        }
    }

    private fun insertCurrentWeather(
        currentWeather: CurrentWeatherResponse?,
        countryName: Address?
    ) {
        currentWeather?.let { currentWeather ->
            currentWeather.latitude =
                locationState.value.latitude.toBigDecimal().setScale(2, RoundingMode.DOWN)
                    .toDouble()
            currentWeather.longitude =
                locationState.value.longitude.toBigDecimal().setScale(2, RoundingMode.DOWN)
                    .toDouble()
            currentWeather.countryName = countryName?.countryName ?: ""
            currentWeather.cityName = countryName?.locality ?: ""
            homeViewModel.insertCurrentWeather(
                currentWeather
            )
        }
    }

    @Composable
    private fun NavHostImpl(
        navController: NavHostController,
        currentWeatherResponse: CurrentWeatherResponse,
        countryName: Address?,
        innerPadding: PaddingValues
    ) {
        NavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = if (LocalStorageDataSource.getInstance(this)
                    .getStartedState
            ) NavigationRoutes.HomeScreen
            else NavigationRoutes.GetStartedScreen,
        ) {
            composable<NavigationRoutes.GetStartedScreen> {
                GetStartedScreen {
                    navController.navigate(NavigationRoutes.HomeScreen)
                }
            }
            composable<NavigationRoutes.HomeScreen> {
                HomeScreen(homeViewModel,bottomNavigationBarViewModel)
            }
            composable<NavigationRoutes.FavoriteScreen> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavigationRoutes.FavoriteScreen)
                }
                val context = LocalContext.current
                val favoriteViewModel = getFavoriteViewModel(parentEntry, context)
                FavoriteScreen(
                    favoriteViewModel,
                    currentWeatherResponse,
                    countryName,
                    onMapClick = {
                        navController.navigate(NavigationRoutes.MapScreen)
                    },
                    onFavoriteCardClicked = { longitude, latitude ->
                        navController.navigate(
                            NavigationRoutes.WeatherDetailsScreen(
                                longitude,
                                latitude
                            )


                        )
                    },bottomNavigationBarViewModel)
            }
            composable<NavigationRoutes.AlarmScreen> {
                AlarmScreen()
            }

            composable<NavigationRoutes.WeatherDetailsScreen> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavigationRoutes.FavoriteScreen)
                }
                val context = LocalContext.current
                val favoriteViewModel = getFavoriteViewModel(parentEntry, context)
                val weatherDetailsScreen =
                    backStackEntry.toRoute<NavigationRoutes.WeatherDetailsScreen>()
                WeatherDetailsScreen(
                    weatherDetailsScreen.longitude,
                    weatherDetailsScreen.latitude,
                    favoriteViewModel,
                    bottomNavigationBarViewModel
                )
            }
            composable<NavigationRoutes.SettingsScreen> {
                SettingsScreen()
            }
            composable<NavigationRoutes.MapScreen> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavigationRoutes.FavoriteScreen)
                }
                val context = LocalContext.current
                val favoriteViewModel = getFavoriteViewModel(parentEntry, context)
                MapScreen(favoriteViewModel, locationState.value,bottomNavigationBarViewModel)
            }
        }
    }

    @Composable
    private fun getFavoriteViewModel(
        parentEntry: NavBackStackEntry,
        context: Context
    ) = ViewModelProvider(
        parentEntry, FavoriteViewModelFactory(
            Repository.getInstance(
                weatherRemoteDataSource = WeatherRemoteDataSource(RetrofitFactory.apiService),
                weatherLocalDataSource = WeatherLocalDataSource(
                    WeatherDatabase.getInstance(
                        context
                    ).getDao()
                )
            )
        )
    )[FavoriteViewModelImpl::class.java]

    override fun onStart() {
        super.onStart()
        if (checkLocationPermission()) {
            if (isLocationEnabled()) {
                getLocation()
            } else {
                enableLocationServices()
            }
        } else {
            requestPermission()
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
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                requestPermission()
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
                homeViewModel.getCurrentWeather(
                    latitude = locationState.value.latitude,
                    longitude = locationState.value.longitude
                )
                homeViewModel.getCountryName(
                    longitude = locationState.value.longitude,
                    latitude = locationState.value.latitude,
                    geocoder = geocoder
                )
                homeViewModel.getFiveDaysWeatherForecast(
                    latitude = locationState.value.latitude,
                    longitude = locationState.value.longitude
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

