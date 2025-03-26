package com.example.weatherapp.favorite.view_model

import android.location.Geocoder
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class FavoriteViewModelImpl(
    private val repository: Repository,
) : ViewModel() {

    private var _selectedWeather: MutableStateFlow<Response> = MutableStateFlow(Response.Loading)
    var selectedWeather = _selectedWeather.asStateFlow()

    private var _selectedFiveDaysWeatherForecast: MutableStateFlow<Response> =
        MutableStateFlow(Response.Loading)

    var selectedFiveDaysWeatherForecast = _selectedFiveDaysWeatherForecast.asStateFlow()

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

    private var _weatherFavorites: MutableStateFlow<Response> =
        MutableStateFlow(Response.Loading)
    var weatherFavorites = _weatherFavorites.asStateFlow()

    private var _fiveDaysForecastFavorites: MutableStateFlow<Response> =
        MutableStateFlow(Response.Loading)
    var fiveDaysForecastFavorites = _fiveDaysForecastFavorites.asStateFlow()


    fun getSelectedWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val result = repository.getCurrentWeather(
                    latitude = latitude, longitude = longitude
                )
                _selectedWeather.emit(Response.Success(result))
                _message.emit("Success")
            } catch (e: Exception) {
                _selectedWeather.emit(Response.Failure(e.message.toString()))
                _message.emit(e.message.toString())
            }
        }
    }

    fun getSelectedFiveDaysWeatherForecast(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val result = repository.getFiveDaysWeatherForecast(
                    latitude = latitude, longitude = longitude
                )

                _selectedFiveDaysWeatherForecast.emit(
                    Response.Success(
                        result.catch { ex ->
                            _selectedFiveDaysWeatherForecast.emit(Response.Failure(ex.message.toString()))
                        }.toList()
                    )
                )
                filterDaysList(result)
            } catch (e: Exception) {
                _selectedFiveDaysWeatherForecast.emit(Response.Failure(e.message.toString()))
                _message.emit(e.message.toString())
            }
        }
    }

    private suspend fun filterDaysList(result: Flow<WeatherItem>) {
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
    }

    fun insertWeather(
        currentWeatherResponse: CurrentWeatherResponse,
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    ) {
        viewModelScope.launch {
            insertSelectedWeather(currentWeatherResponse)
            insertSelectedFiveDaysWeather(fiveDaysWeatherForecastResponse)
        }
    }

    private fun insertSelectedWeather(currentWeatherResponse: CurrentWeatherResponse) {
        viewModelScope.launch {
            val result = repository.insertCurrentWeather(currentWeatherResponse)
            if (result == 1L) {
                _message.emit("Added Successfully")
            } else {
                _message.emit("Something wrong happened!!")
            }
        }
    }

    private fun insertSelectedFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse) {
        viewModelScope.launch {
            val result = repository.insertFiveDaysWeather(fiveDaysWeatherForecastResponse)
            if (result == 1L) {
                _message.emit("Added Successfully")

            } else {
                _message.emit("Something wrong happened!!")
            }
        }
    }

    fun updateWeather(
        currentWeatherResponse: CurrentWeatherResponse,
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    ) {
        viewModelScope.launch {
            updateSelectedWeather(currentWeatherResponse)
            updateSelectedFiveDaysWeather(fiveDaysWeatherForecastResponse)
        }
    }
    private fun updateSelectedWeather(currentWeatherResponse: CurrentWeatherResponse) {
        viewModelScope.launch {
            val result = repository.updateCurrentWeather(currentWeatherResponse)
            if (result == 1) {
                _message.emit("Updated Successfully")
            } else {
                _message.emit("Something wrong happened!!")
            }
        }
    }

    private fun updateSelectedFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse) {
        viewModelScope.launch {
            val result = repository.updateFiveDaysWeather(fiveDaysWeatherForecastResponse)
            if (result == 1) {
                _message.emit("Updated Successfully")

            } else {
                _message.emit("Something wrong happened!!")
            }
        }
    }

    fun getCountryName(longitude: Double, latitude: Double, geocoder: Geocoder) {
        viewModelScope.launch {
            val list = geocoder.getFromLocation(latitude, longitude, 1)
            if (!list.isNullOrEmpty()) _countryName.emit(Response.Success(list[0]))
        }
    }

    fun deleteWeather(
        currentWeatherResponse: CurrentWeatherResponse,
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    ) {
        viewModelScope.launch {
            deleteCurrentWeather(currentWeatherResponse)
            deleteFiveDaysWeather(fiveDaysWeatherForecastResponse)
        }
    }

    private fun deleteCurrentWeather(currentWeatherResponse: CurrentWeatherResponse) {
        viewModelScope.launch {
            val result = repository.deleteCurrentWeather(currentWeatherResponse)
            if (result == 0) {
                _message.emit("Deleted Successfully")
            } else {
                _message.emit("Something wrong happened!!")

            }
        }
    }

    private fun deleteFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse) {
        viewModelScope.launch {
            val result = repository.deleteFiveDaysWeather(fiveDaysWeatherForecastResponse)
            if (result == 0) {
                _message.emit("Deleted Successfully")
            } else {
                _message.emit("Something wrong happened!!")

            }
        }
    }

    fun selectFavorites() {
        viewModelScope.launch {

            selectAllFavorites()
            selectAllFiveDaysWeatherFromFavorites()
        }

    }

    private fun selectAllFavorites() {
        viewModelScope.launch {
            try {
                val result = repository.selectAllFavorites()
                result.catch { ex ->
                    _weatherFavorites.emit(Response.Failure(ex.message.toString()))
                }.collect {
                   _weatherFavorites.emit(Response.Success(it))
                }
            } catch (e: Exception) {
                _weatherFavorites.emit(Response.Failure(e.message.toString()))
            }
        }
    }

    private fun selectAllFiveDaysWeatherFromFavorites() {
        viewModelScope.launch {
            try {
                val result = repository.selectAllFiveDaysWeatherFromFavorites()
                result.catch { ex ->
                    _fiveDaysForecastFavorites.emit(Response.Failure(ex.message.toString()))
                }.collect {
                    _fiveDaysForecastFavorites.emit(Response.Success(it))
                }
            } catch (e: Exception) {
                _fiveDaysForecastFavorites.emit(Response.Failure(e.message.toString()))
            }
        }
    }

}

class FavoriteViewModelFactory(
    private val repository: Repository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteViewModelImpl(repository) as T
    }
}

