package com.example.weatherapp.favorite.view_model

import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.utilis.formatDateTime
import com.example.weatherapp.utilis.getCurrentDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.util.Locale

class FavoriteViewModelImpl(
    private val repository: Repository,
) : ViewModel() {

    private var _selectedWeather: MutableStateFlow<Response> = MutableStateFlow(Response.Loading)
    var selectedWeather = _selectedWeather.asStateFlow()

    private var _selectedFiveDaysWeatherForecast: MutableStateFlow<Response> =
        MutableStateFlow(Response.Loading)

    var selectedFiveDaysWeatherForecast = _selectedFiveDaysWeatherForecast.asStateFlow()

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

    private var _deleteFavoriteItemResult: MutableStateFlow<String> = MutableStateFlow("")
    var deleteFavoriteItemResult = _deleteFavoriteItemResult.asStateFlow()

    private var _insertFavoriteItemResult: MutableStateFlow<String> = MutableStateFlow("")
    var insertFavoriteItemResult = _insertFavoriteItemResult.asStateFlow()

    private var _updateFavoriteResult: MutableStateFlow<String> = MutableStateFlow("")
    var updateFavoriteResult = _updateFavoriteResult.asStateFlow()


    private var _alarms: MutableStateFlow<Response> = MutableStateFlow(Response.Loading)
    var alarms = _alarms.asStateFlow()

    fun getSelectedWeather(latitude: Double, longitude: Double, isConnected: Boolean) {
        viewModelScope.launch {
            if (isConnected) {
                try {
                    val result = repository.getCurrentWeather(
                        latitude = latitude, longitude = longitude
                    )
                    _selectedWeather.emit(Response.Success(result))
                } catch (e: Exception) {
                    _selectedWeather.emit(Response.Failure(e.message.toString()))
                }
            } else {
                selectDayWeather(longitude = longitude, latitude = latitude)
            }
        }
    }

    fun getSelectedFiveDaysWeatherForecast(
        latitude: Double,
        longitude: Double,
        isConnected: Boolean
    ) {
        viewModelScope.launch {
            if (isConnected) {
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
                }
            } else {
                selectFiveDaysWeather(longitude = longitude, latitude = latitude)
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
            val resultOne = repository.insertCurrentWeather(currentWeatherResponse)
            val resultTwo = repository.insertFiveDaysWeather(fiveDaysWeatherForecastResponse)

            if (resultOne > 0L && resultTwo > 0L) {
                _insertFavoriteItemResult.emit("Added Successfully")

            } else {
                _insertFavoriteItemResult.emit("Something wrong happened!!")
            }
        }
    }


    fun updateWeather(
        currentWeatherResponse: CurrentWeatherResponse,
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    ) {
        viewModelScope.launch {
            val resultOne = repository.updateCurrentWeather(currentWeatherResponse)
            val resultTwo = repository.updateFiveDaysWeather(fiveDaysWeatherForecastResponse)
            if (resultOne > 0 && resultTwo > 0) {
                _updateFavoriteResult.emit("Weather Updated Successfully")
            } else {
                _updateFavoriteResult.emit("Something wrong happened!!")

            }
        }
    }


    fun getCountryName(
        longitude: Double,
        latitude: Double,
        geocoder: Geocoder,
        isConnected: Boolean
    ) {
        viewModelScope.launch {
            if (isConnected) {
                val list = geocoder.getFromLocation(latitude, longitude, 1)
                if (!list.isNullOrEmpty()) _countryName.emit(Response.Success(list[0]))
            } else {
                _countryName.emit(Response.Success(Address(Locale(""))))
            }
        }
    }

    fun deleteWeather(
        currentWeatherResponse: CurrentWeatherResponse,
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    ) {
        viewModelScope.launch {
            val resultOne = repository.deleteCurrentWeather(currentWeatherResponse)
            val resultTwo = repository.deleteFiveDaysWeather(fiveDaysWeatherForecastResponse)

            if (resultOne > 0 && resultTwo > 0) {
                _deleteFavoriteItemResult.emit("Deleted Successfully")
            } else {
                _deleteFavoriteItemResult.emit("Something wrong happened!!")

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

    private fun selectDayWeather(longitude: Double, latitude: Double) {
        viewModelScope.launch {
            val result = repository.selectDayWeather(
                longitude = longitude.toBigDecimal()
                    .setScale(2, RoundingMode.DOWN).toDouble(), latitude = latitude.toBigDecimal()
                    .setScale(2, RoundingMode.DOWN).toDouble()
            )
            result.collect {
                _selectedWeather.emit(Response.Success(it))
            }
        }
    }

    private fun selectFiveDaysWeather(longitude: Double, latitude: Double) {
        viewModelScope.launch {
            val result =
                repository.selectFiveDaysWeather(
                    longitude = longitude.toBigDecimal()
                        .setScale(2, RoundingMode.DOWN).toDouble(),
                    latitude = latitude.toBigDecimal()
                        .setScale(2, RoundingMode.DOWN).toDouble()
                )
            result.collect {
                _selectedFiveDaysWeatherForecast.emit(Response.Success(it.list))
                filterDaysList(it.list.asFlow())
            }
        }
    }

    fun selectAllAlarms() {
        viewModelScope.launch {
            try {
                val result = repository.selectAllAlarms()
                result.catch { ex ->
                    _alarms.emit(Response.Failure(ex.message.toString()))
                }.collect {
                    _alarms.emit(Response.Success(it))
                }
            } catch (e: Exception) {
                _alarms.emit(Response.Failure(e.message.toString()))
            }
        }
    }

    fun insertAlarm(alarm: AlarmModel) {
        viewModelScope.launch {
            val result = repository.insertAlarm(alarm)
        }
    }

    fun deleteAlarm(alarm: AlarmModel) {
        viewModelScope.launch {
            val result = repository.deleteAlarm(alarm)
        }
    }

    fun updateAlarm(alarm: AlarmModel) {
        viewModelScope.launch {
            val result = repository.updateAlarm(alarm)
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

