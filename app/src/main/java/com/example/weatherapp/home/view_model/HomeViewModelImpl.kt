package com.example.weatherapp.home.view_model

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.utilis.formatDateTime
import com.example.weatherapp.utilis.getCurrentDate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch


class HomeViewModelImpl(
    private val repository: Repository,
) : ViewModel() {

    private var _currentWeather: MutableLiveData<CurrentWeatherResponse> = MutableLiveData()
    var currentWeather: LiveData<CurrentWeatherResponse> = _currentWeather

    private var _fiveDaysWeatherForecast: MutableLiveData<List<WeatherItem>> =
        MutableLiveData()
    var fiveDaysWeatherForecast: LiveData<List<WeatherItem>> =
        _fiveDaysWeatherForecast

    private var _message: MutableLiveData<String> = MutableLiveData()
    var message: LiveData<String> = _message

    private var _countryName: MutableLiveData<Address> = MutableLiveData()
    var countryName: LiveData<Address> = _countryName

    private var _currentDayList: MutableLiveData<List<WeatherItem>> = MutableLiveData()
    var currentDayList: LiveData<List<WeatherItem>> = _currentDayList

    private var _nextDayList: MutableLiveData<List<WeatherItem>> = MutableLiveData()
    var nextDayList: LiveData<List<WeatherItem>> = _nextDayList

    private var _thirdDayList: MutableLiveData<List<WeatherItem>> = MutableLiveData()
    var thirdDayList: LiveData<List<WeatherItem>> = _thirdDayList

    private var _fourthDayList: MutableLiveData<List<WeatherItem>> = MutableLiveData()
    var fourthDayList: LiveData<List<WeatherItem>> = _fourthDayList
    private var _fifthDayList: MutableLiveData<List<WeatherItem>> = MutableLiveData()
    var fifthDayList: LiveData<List<WeatherItem>> = _fifthDayList

    private var _sixthDayList: MutableLiveData<List<WeatherItem>> = MutableLiveData()
    var sixthDayList: LiveData<List<WeatherItem>> = _sixthDayList



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

                _fiveDaysWeatherForecast.value = response.toList()
                _currentDayList.value =
                    response.filter { formatDateTime(it.dt_txt) == getCurrentDate(0) }.toList()
                _nextDayList.value =
                    response.filter { formatDateTime(it.dt_txt) == getCurrentDate(1) }.toList()
                _thirdDayList.value =
                    response.filter { formatDateTime(it.dt_txt) == getCurrentDate(2) }.toList()
                _fourthDayList.value =
                    response.filter { formatDateTime(it.dt_txt) == getCurrentDate(3) }.toList()
                _fifthDayList.value =
                    response.filter { formatDateTime(it.dt_txt) == getCurrentDate(4) }.toList()
                _sixthDayList.value =
                    response.filter { formatDateTime(it.dt_txt) == getCurrentDate(5) }.toList()


            } catch (e: Exception) {

                _message.postValue(e.message)

            }

        }


    }

    fun insertCurrentWeather(currentWeatherResponse: CurrentWeatherResponse) {
        viewModelScope.launch {
            repository.insertCurrentWeather(currentWeatherResponse)
        }

    }

    fun insertFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse) {
        viewModelScope.launch {
            repository.insertFiveDaysWeather(fiveDaysWeatherForecastResponse)
        }

    }

    fun getCountryName(longitude: Double, latitude: Double,geocoder: Geocoder) {
        viewModelScope.launch {
            val list =
                geocoder.getFromLocation(latitude, longitude, 1)
            if (!list.isNullOrEmpty())
                _countryName.postValue(list[0])
        }
    }




}

class HomeViewModelFactory(
    private val repository: Repository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModelImpl(repository) as T
    }

}