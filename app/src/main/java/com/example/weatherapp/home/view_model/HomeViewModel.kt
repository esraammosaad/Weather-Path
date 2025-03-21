package com.example.weatherapp.home.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FivedaysWeatherForecastResponse
import com.example.weatherapp.data.repository.Repository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private var _currentWeather: MutableLiveData<CurrentWeatherResponse> = MutableLiveData()
    var currentWeather: LiveData<CurrentWeatherResponse> = _currentWeather

    private var _fiveDaysWeatherForecast: MutableLiveData<FivedaysWeatherForecastResponse> =
        MutableLiveData()
    var fiveDaysWeatherForecast: LiveData<FivedaysWeatherForecastResponse> =
        _fiveDaysWeatherForecast

    private var _message: MutableLiveData<String> = MutableLiveData()
    var message: LiveData<String> = _message

    fun getCurrentWeather(latitude: Double, longitude: Double) {

        viewModelScope.launch {

            try {
                val response =
                    repository.getCurrentWeather(latitude = latitude, longitude = longitude)
                if (response.isSuccessful) {

                    _currentWeather.postValue(response.body())
                    _message.postValue("Success")

                } else {

                    _message.postValue("something wrong happen please try again!!")
                }
            } catch (e: Exception) {

                _message.postValue(e.message)

            }

        }


    }

    fun getFiveDaysWeatherForecast(latitude: Double, longitude: Double) {

        viewModelScope.launch {

            try {
                val response =
                    repository.getFiveDaysWeatherForecast(
                        latitude = latitude,
                        longitude = longitude
                    )
                if (response.isSuccessful) {

                    _fiveDaysWeatherForecast.postValue(response.body())
                } else {

                    _message.postValue("something wrong happen please try again!!")
                }
            } catch (e: Exception) {

                _message.postValue(e.message)

            }

        }


    }


}

class HomeViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }

}