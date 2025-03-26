package com.example.weatherapp.home.view_model

import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.utilis.formatDateTime
import com.example.weatherapp.utilis.getCurrentDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch


class HomeViewModelImpl(
    private val repository: Repository,
) : ViewModel() {

    private var _currentWeather: MutableStateFlow<Response> = MutableStateFlow(Response.Loading)
    var currentWeather = _currentWeather.asStateFlow()

    private var _fiveDaysWeatherForecast: MutableStateFlow<Response> =
        MutableStateFlow(Response.Loading)

    var fiveDaysWeatherForecast = _fiveDaysWeatherForecast.asStateFlow()

    private var _message: MutableStateFlow<String> = MutableStateFlow("")
    var message = _message.asStateFlow()

    private var _countryName: MutableStateFlow<Response> = MutableStateFlow(Response.Loading)
    var countryName = _countryName.asStateFlow()

    private var _currentDayList: MutableStateFlow<List<WeatherItem>> = MutableStateFlow(emptyList())
    var currentDayList = _currentDayList.asStateFlow()

    private var _nextDayList: MutableStateFlow<List<WeatherItem>> = MutableStateFlow(emptyList())
    var nextDayList = _nextDayList.asStateFlow()

    private var _thirdDayList: MutableStateFlow<List<WeatherItem>> = MutableStateFlow(emptyList())
    var thirdDayList = _thirdDayList.asStateFlow()

    private var _fourthDayList: MutableStateFlow<List<WeatherItem>> = MutableStateFlow(emptyList())
    var fourthDayList = _fourthDayList.asStateFlow()
    private var _fifthDayList: MutableStateFlow<List<WeatherItem>> = MutableStateFlow(emptyList())
    var fifthDayList = _fifthDayList.asStateFlow()

    private var _sixthDayList: MutableStateFlow<List<WeatherItem>> = MutableStateFlow(emptyList())
    var sixthDayList = _sixthDayList.asStateFlow()

    private var _favoriteItems: MutableStateFlow<List<WeatherItem>> = MutableStateFlow(emptyList())
    var favoriteItems = _favoriteItems.asStateFlow()


    fun getCurrentWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val result = repository.getCurrentWeather(
                    latitude = latitude,
                    longitude = longitude
                )

                _currentWeather.emit(Response.Success(result))
                _message.emit("Success")

                Log.i("TAG", "getSelectedWeather: nnnnnnnnnnnn")

//                    _message.postValue("something wrong happen please try again!!")

            } catch (e: Exception) {

                _currentWeather.emit(Response.Failure(e.message.toString()))
                _message.emit(e.message.toString())
                Log.i("TAG", "getSelectedWeather: nnnnnnnnnnnn")


            }

        }


    }

    fun getFiveDaysWeatherForecast(latitude: Double, longitude: Double) {

        viewModelScope.launch {
            try {
                val result =
                    repository.getFiveDaysWeatherForecast(
                        latitude = latitude,
                        longitude = longitude
                    )

                _fiveDaysWeatherForecast.emit(Response.Success(result.catch { ex ->
                    _fiveDaysWeatherForecast.emit(Response.Failure(ex.message.toString()))

                }.toList()))
                Log.i("TAG", "getSelectedWeather: nnnnnnnnnnnn")

                _currentDayList.emit(result.filter { formatDateTime(it.dt_txt) == getCurrentDate(0) }
                    .toList())
                _nextDayList.emit(result.filter { formatDateTime(it.dt_txt) == getCurrentDate(1) }
                    .toList())
                _thirdDayList.emit(result.filter { formatDateTime(it.dt_txt) == getCurrentDate(2) }
                    .toList())
                _fourthDayList.emit(result.filter { formatDateTime(it.dt_txt) == getCurrentDate(3) }
                    .toList())
                _fifthDayList.emit(result.filter { formatDateTime(it.dt_txt) == getCurrentDate(4) }
                    .toList())
                _sixthDayList.emit(result.filter { formatDateTime(it.dt_txt) == getCurrentDate(5) }
                    .toList())


            } catch (e: Exception) {

                _message.emit(e.message.toString())
                Log.i("TAG", "getSelectedWeather: nnnnnnnnnnnn")


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

    fun getCountryName(longitude: Double, latitude: Double, geocoder: Geocoder) {
        viewModelScope.launch {
            val list =
                geocoder.getFromLocation(latitude, longitude, 1)
            if (!list.isNullOrEmpty())
                _countryName.emit(Response.Success(list[0]))
        }
    }

    private fun selectDayWeather(longitude: Double, latitude: Double) {
        viewModelScope.launch {
            val result = repository.selectDayWeather(longitude = longitude, latitude = latitude)
            _currentWeather.emit(Response.Success(result))

            Log.i("TAG", "==== selectDayWeather: $result")
            Log.i("TAG", "selectDayWeather: $latitude , $longitude")


        }
    }
    private fun selectFiveDaysWeather(longitude: Double, latitude: Double) {
        viewModelScope.launch {
            val result = repository.selectFiveDaysWeather(longitude = longitude, latitude = latitude)
            result.collect{

                _fiveDaysWeatherForecast.emit(Response.Success(it.list))

            }

            Log.i("TAG", "==== selectDayWeather: $result")
            Log.i("TAG", "selectDayWeather: $latitude , $longitude")


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