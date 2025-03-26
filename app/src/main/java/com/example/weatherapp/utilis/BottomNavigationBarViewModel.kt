package com.example.weatherapp.utilis

import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BottomNavigationBarViewModel : ViewModel() {

    private var _currentWeatherTheme: MutableStateFlow<Brush> =
        MutableStateFlow(getWeatherGradient())
    var currentWeatherTheme = _currentWeatherTheme.asStateFlow()

    fun setCurrentWeatherTheme(icon: String) {

        viewModelScope.launch {

            _currentWeatherTheme.emit(getWeatherGradient(icon))

        }

    }


}