package com.example.weatherapp.utilis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.weatherapp.R
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.favorite.view_model.FavoriteViewModelImpl

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    favoriteViewModel: FavoriteViewModelImpl,
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


    val selectedItem = rememberSaveable { mutableIntStateOf(0) }
    val dynamicBrush =
        bottomNavigationBarViewModel.currentWeatherTheme.collectAsStateWithLifecycle().value
    favoriteViewModel.selectAllAlarms()
    val res = favoriteViewModel.alarms.collectAsStateWithLifecycle().value

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
                icon = {
                    BadgedBox(
                        badge = {
                            if (index == 2)
                                Badge(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                    ) {
                                    when(res){
                                        is Response.Failure -> {}
                                        Response.Loading -> {}
                                        is Response.Success<*> -> {
                                            res as Response.Success<List<AlarmModel>>
                                            Text(res.result?.size.toString())
                                        }
                                    }

                                }
                        }
                    ) {
                        Icon(painter = painterResource(item.icon), contentDescription = "")
                    }


                },
                label = { Text(titleList[index]) },
                alwaysShowLabel = false,
                colors = NavigationBarItemColors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    selectedIndicatorColor = Color.Transparent,
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                    disabledIconColor = Color.White,
                    disabledTextColor = Color.White
                )

            )

        }
    }


}