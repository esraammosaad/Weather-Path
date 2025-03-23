package com.example.weatherapp.utilis

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

@Composable
fun BottomNavigationBar(navController: NavHostController, icon:String) {

    val screensList = arrayOf(
        NavigationRoutes.HomeScreen,
        NavigationRoutes.FavoriteScreen,
        NavigationRoutes.AlarmScreen,
        NavigationRoutes.SettingsScreen
    )

    val selectedItem = remember { mutableIntStateOf(0) }

    NavigationBar(

        modifier = Modifier.background(brush = getWeatherGradient(icon)),
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
                label = { Text(item.title) },
                alwaysShowLabel = false,
                colors = NavigationBarItemColors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    selectedIndicatorColor = Color.Transparent,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    disabledIconColor = Color.White,
                    disabledTextColor = Color.White
                )

            )

        }
    }


}