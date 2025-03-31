package com.example.weatherapp.utilis

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.weatherapp.R
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    bottomNavigationBarViewModel: BottomNavigationBarViewModel
) {

    val screensList = arrayOf(
        NavigationRoutes.HomeScreen,
        NavigationRoutes.FavoriteScreen,
        NavigationRoutes.AlarmScreen,
        NavigationRoutes.SettingsScreen
    )

    val titleList = arrayOf(
        stringResource(R.string.weather),
        stringResource(R.string.favorites),
        stringResource(R.string.alarms),
        stringResource(R.string.preferences)
    )

    val selectedItem = remember { mutableIntStateOf(0) }
    val dynamicBrush =
        bottomNavigationBarViewModel.currentWeatherTheme.collectAsStateWithLifecycle().value

    NavigationBar(
        modifier = Modifier.background(
            brush = dynamicBrush,
        ),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,

        ) {
        screensList.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem.intValue == index,
                onClick = {
                    selectedItem.intValue = index
                    navController.navigate(item) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                        restoreState = true

                    }
                },
                icon = { Icon(painter = painterResource(item.icon), contentDescription = "") },
                label = { Text(titleList[index]) },
                alwaysShowLabel = false,
                colors = NavigationBarItemColors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    selectedIndicatorColor = Color.Transparent,
                    unselectedIconColor = Color.White.copy(alpha = 0.5f),
                    unselectedTextColor = Color.White.copy(alpha = 0.5f),
                    disabledIconColor = Color.White,
                    disabledTextColor = Color.White
                )

            )

        }
    }


}