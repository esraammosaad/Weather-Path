package com.example.weatherapp.utilis

import android.content.Context
import android.location.Address
import android.location.Location
import android.os.Build
import android.util.Log
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
import com.example.weatherapp.alarm.view.AlarmScreen
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSourceImpl
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.remote.RetrofitFactory
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.favorite.view.screens.FavoriteScreen
import com.example.weatherapp.favorite.view.screens.MapScreen
import com.example.weatherapp.favorite.view.screens.WeatherDetailsScreen
import com.example.weatherapp.favorite.view_model.FavoriteViewModelFactory
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl
import com.example.weatherapp.home.view.HomeScreen
import com.example.weatherapp.home.view.LocationPickScreen
import com.example.weatherapp.home.view_model.HomeViewModelImpl
import com.example.weatherapp.settings.view.SettingsScreen
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavHostImpl(
    navController: NavHostController,
    currentWeatherResponse: CurrentWeatherResponse,
    countryName: Address?,
    innerPadding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    homeViewModel: HomeViewModelImpl,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel,
    locationState: MutableState<Location>,
    isConnected: Boolean,
    isLocationPickedScreen: MutableState<Boolean>,
    isMainScreen: MutableState<Boolean>,
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
                    isConnected = isConnected
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
                isConnected
            )
        }
        composable<NavigationRoutes.SettingsScreen> {
            SettingsScreen(
                homeViewModel,
                currentWeatherResponse,
                snackBarHostState,
                bottomNavigationBarViewModel,
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
                    scope.launch {
                        snackBarHostState.showSnackbar("Location Updated Successfully")
                    }
//                    (context as Activity).recreate()
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
)[FavoriteViewModelImpl::class.java]

