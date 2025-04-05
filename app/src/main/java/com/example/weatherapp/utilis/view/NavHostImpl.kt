package com.example.weatherapp.utilis.view

import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.weatherapp.R
import com.example.weatherapp.favorite_alarm_features.alarm.view.AlarmScreen
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSourceImpl
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.remote.RetrofitFactory
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.favorite_alarm_features.favorite.view.FavoriteScreen
import com.example.weatherapp.favorite_alarm_features.favorite.view.MapScreen
import com.example.weatherapp.favorite_alarm_features.favorite.view.WeatherDetailsScreen
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteViewModelFactory
import com.example.weatherapp.favorite_alarm_features.view_model.FavoriteAndAlarmSharedViewModelImpl
import com.example.weatherapp.home_settings_feature.home.view.HomeScreen
import com.example.weatherapp.home_settings_feature.settings.view.LocationPickScreen
import com.example.weatherapp.home_settings_feature.view_model.HomeAndSettingsSharedViewModelImpl
import com.example.weatherapp.home_settings_feature.settings.view.SettingsScreen
import com.example.weatherapp.utilis.BottomNavigationBarViewModel
import com.example.weatherapp.utilis.NavigationRoutes
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavHostImpl(
    navController: NavHostController,
    currentWeatherResponse: CurrentWeatherResponse,
    countryName: Address?,
    innerPadding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    homeViewModel: HomeAndSettingsSharedViewModelImpl,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel,
    locationState: MutableState<Location>,
    isConnected: Boolean,
) {
    NavHost(
        navController = navController,
        modifier = Modifier.padding(innerPadding),
        startDestination = NavigationRoutes.HomeScreen

    ) {
        composable<NavigationRoutes.HomeScreen> {
            HomeScreen(homeViewModel, bottomNavigationBarViewModel, snackBarHostState)
        }
        composable<NavigationRoutes.FavoriteScreen> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(NavigationRoutes.FavoriteScreen)
            }
            val context = LocalContext.current
            val favoriteViewModel = getFavoriteViewModel(parentEntry, context)
            if (countryName != null) {
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
                    },
                    bottomNavigationBarViewModel = bottomNavigationBarViewModel,
                    snackBarHostState = snackBarHostState,
                )
            }
        }
        composable<NavigationRoutes.AlarmScreen> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(NavigationRoutes.AlarmScreen)
            }
            val context = LocalContext.current
            val favoriteViewModel = getFavoriteViewModel(parentEntry, context)
            AlarmScreen(
                favoriteViewModel,
                currentWeatherResponse,
                snackBarHostState,
                bottomNavigationBarViewModel
            )
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
                bottomNavigationBarViewModel,
                isConnected,
                snackBarHostState
            )
        }
        composable<NavigationRoutes.SettingsScreen> {
            SettingsScreen(
                homeViewModel,
                currentWeatherResponse,
                snackBarHostState,
                isConnected
            ) {
                navController.navigate(NavigationRoutes.LocationPickScreen)
            }
        }
        composable<NavigationRoutes.LocationPickScreen> {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            LocationPickScreen(
                homeViewModel = homeViewModel,
                bottomNavigationBarViewModel = bottomNavigationBarViewModel,
                onBackClicked = {
                    navController.popBackStack()
                },
                location = locationState,
                isConnected = isConnected,
                onChooseClicked = {
                    navController.popBackStack()
                    (context as Activity).recreate()
                    scope.launch {
                        snackBarHostState.showSnackbar(context.getString(R.string.location_updated_successfully))
                    }
                }
            )
        }
        composable<NavigationRoutes.MapScreen> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(NavigationRoutes.FavoriteScreen)
            }
            val context = LocalContext.current
            val favoriteViewModel = getFavoriteViewModel(parentEntry, context)
            MapScreen(
                favoriteViewModel,
                locationState.value,
                bottomNavigationBarViewModel,
                snackBarHostState = snackBarHostState,
                isConnected
            )
        }
    }
}

@Composable
fun getFavoriteViewModel(
    parentEntry: NavBackStackEntry,
    context: Context
) = ViewModelProvider(
    parentEntry, FavoriteViewModelFactory(
        WeatherRepositoryImpl.getInstance(
            weatherRemoteDataSourceImpl = WeatherRemoteDataSourceImpl(RetrofitFactory.apiService),
            weatherLocalDataSourceImpl = WeatherLocalDataSourceImpl(
                WeatherDatabase.getInstance(
                    context
                ).getDao()
            )
        )
    )
)[FavoriteAndAlarmSharedViewModelImpl::class.java]

