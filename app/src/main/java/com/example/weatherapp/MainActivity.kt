package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.alarm.view.AlarmScreen
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.favorite.view.FavoriteScreen
import com.example.weatherapp.home.view.HomeScreen
import com.example.weatherapp.home.view_model.HomeViewModelImpl
import com.example.weatherapp.home.view_model.HomeViewModelFactory
import com.example.weatherapp.landing.view.GetStartedScreen
import com.example.weatherapp.settings.view.SettingsScreen
import com.example.weatherapp.utilis.BottomNavigationBar
import com.example.weatherapp.utilis.NavigationRoutes
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


private const val My_LOCATION_PERMISSION_ID = 5005

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationState: MutableState<Location>
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationState = mutableStateOf(Location(LocationManager.GPS_PROVIDER))
        geocoder = Geocoder(this)



        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            val viewModel: HomeViewModelImpl = viewModel<HomeViewModelImpl>(
                factory = HomeViewModelFactory(
                    Repository.getInstance(
                        weatherRemoteDataSource = WeatherRemoteDataSource(),
                        weatherLocalDataSource = WeatherLocalDataSource()
                    )
                )
            )

            viewModel.getCurrentWeather(
                latitude = locationState.value.latitude,
                longitude = locationState.value.longitude
            )
            viewModel.getCountryName(
                geocoder = geocoder,
                longitude = locationState.value.longitude,
                latitude = locationState.value.latitude
            )
            viewModel.getFiveDaysWeatherForecast(
                latitude = locationState.value.latitude,
                longitude = locationState.value.longitude
            )
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry.value?.destination


            Scaffold(


                contentWindowInsets = WindowInsets(0.dp),
                bottomBar = {
                    if (NavigationRoutes.GetStartedScreen::class.simpleName?.let {
                            currentDestination?.route?.contains(
                                it
                            )
                        } == true) {
                        Unit
                    } else BottomNavigationBar(navController,viewModel.currentWeather.value?.weather?.get(0)?.icon?:"")

                },
                modifier = Modifier.fillMaxSize()

            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = NavigationRoutes.GetStartedScreen,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable<NavigationRoutes.GetStartedScreen> {
                        GetStartedScreen {
                            navController.navigate(NavigationRoutes.HomeScreen)
                        }
                    }
                    composable<NavigationRoutes.HomeScreen> {
                        HomeScreen(viewModel)
                    }
                    composable<NavigationRoutes.FavoriteScreen> {
                        FavoriteScreen()
                    }
                    composable<NavigationRoutes.AlarmScreen> {
                        AlarmScreen()
                    }
                    composable<NavigationRoutes.SettingsScreen> {
                        SettingsScreen()
                    }
                }


            }


        }
    }

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
    private fun getLocation() {
        val locationRequest = LocationRequest.Builder(100000).apply {
            setPriority(Priority.PRIORITY_LOW_POWER)
            setMinUpdateIntervalMillis(100000)
        }.build()

        val locationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationState.value =
                    locationResult.lastLocation ?: Location(LocationManager.GPS_PROVIDER)
            }


        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.myLooper()
        )

    }

}