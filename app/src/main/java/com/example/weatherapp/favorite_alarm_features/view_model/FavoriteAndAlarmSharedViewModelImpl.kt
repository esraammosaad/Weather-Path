package com.example.weatherapp.favorite_alarm_features.view_model

import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.data.model.AlarmModel
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.utilis.formatDateTime
import com.example.weatherapp.utilis.getCurrentDate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.util.Locale

class FavoriteAndAlarmSharedViewModelImpl(
    private val weatherRepositoryImpl: WeatherRepository,
) : ViewModel(), FavoriteAndAlarmSharedViewModel {

    private var _selectedWeather: MutableStateFlow<Response> = MutableStateFlow(Response.Loading)
    override var selectedWeather = _selectedWeather.asStateFlow()

    private var _selectedFiveDaysWeatherForecast: MutableStateFlow<Response> =
        MutableStateFlow(Response.Loading)

    override var selectedFiveDaysWeatherForecast = _selectedFiveDaysWeatherForecast.asStateFlow()

    private var _countryName: MutableStateFlow<Response> = MutableStateFlow(Response.Loading)
    override var countryName = _countryName.asStateFlow()

    private var _currentDayList: MutableStateFlow<List<WeatherItem>> = MutableStateFlow(emptyList())
    override var currentDayList = _currentDayList.asStateFlow()

    private var _nextDayList: MutableStateFlow<List<WeatherItem>> = MutableStateFlow(emptyList())
    override var nextDayList = _nextDayList.asStateFlow()

    private var _thirdDayList: MutableStateFlow<List<WeatherItem>> = MutableStateFlow(emptyList())
    override var thirdDayList = _thirdDayList.asStateFlow()

    private var _fourthDayList: MutableStateFlow<List<WeatherItem>> = MutableStateFlow(emptyList())
    override var fourthDayList = _fourthDayList.asStateFlow()
    private var _fifthDayList: MutableStateFlow<List<WeatherItem>> = MutableStateFlow(emptyList())
    override var fifthDayList = _fifthDayList.asStateFlow()

    private var _sixthDayList: MutableStateFlow<List<WeatherItem>> = MutableStateFlow(emptyList())
    override var sixthDayList = _sixthDayList.asStateFlow()

    private var _weatherFavorites: MutableStateFlow<Response> =
        MutableStateFlow(Response.Loading)
    override var weatherFavorites = _weatherFavorites.asStateFlow()

    private var _fiveDaysForecastFavorites: MutableStateFlow<Response> =
        MutableStateFlow(Response.Loading)
    override var fiveDaysForecastFavorites = _fiveDaysForecastFavorites.asStateFlow()

    private var _message: MutableSharedFlow<Int> = MutableSharedFlow()
    override var message = _message.asSharedFlow()

    private var _alarms: MutableStateFlow<Response> = MutableStateFlow(Response.Loading)
    override var alarms = _alarms.asStateFlow()

    private var _searchText: MutableSharedFlow<String> = MutableSharedFlow()
    var searchText = _searchText.asSharedFlow()

    override fun getSelectedWeather(
        latitude: Double, longitude: Double, isConnected: Boolean, languageCode: String,
        tempUnit: String
    ) {
        viewModelScope.launch {
            if (isConnected) {
                try {
                    val result = weatherRepositoryImpl.getCurrentWeather(
                        latitude = latitude,
                        longitude = longitude,
                        languageCode = languageCode,
                        tempUnit = tempUnit
                    )
                    result.catch { ex ->
                        _selectedWeather.emit(Response.Failure(ex.message.toString()))

                    }.collect {
                        _selectedWeather.emit(Response.Success(it))
                    }
                } catch (e: Exception) {
                    _selectedWeather.emit(Response.Failure(e.message.toString()))
                }
            } else {
                selectDayWeather(longitude = longitude, latitude = latitude)
            }
        }
    }

    override fun getSelectedFiveDaysWeatherForecast(
        latitude: Double,
        longitude: Double,
        isConnected: Boolean,
        languageCode: String,
        tempUnit: String
    ) {
        viewModelScope.launch {
            if (isConnected) {
                try {
                    val result = weatherRepositoryImpl.getFiveDaysWeatherForecast(
                        latitude = latitude,
                        longitude = longitude,
                        languageCode = languageCode,
                        tempUnit = tempUnit
                    )
                    result.catch { ex ->
                        _selectedFiveDaysWeatherForecast.emit(Response.Failure(ex.message.toString()))
                        _message.emit(R.string.something_wrong_happened)
                        selectFiveDaysWeather(
                            longitude = longitude,
                            latitude = latitude
                        )
                    }.collect {
                        _selectedFiveDaysWeatherForecast.emit(Response.Success(it))
                        filterDaysList(it)

                    }

                } catch (e: Exception) {
                    _selectedFiveDaysWeatherForecast.emit(Response.Failure(e.message.toString()))
                    _message.emit(R.string.something_wrong_happened)
                    selectFiveDaysWeather(
                        longitude = longitude,
                        latitude = latitude
                    )
                }
            } else {
                selectFiveDaysWeather(longitude = longitude, latitude = latitude)
            }
        }
    }

    private suspend fun filterDaysList(result: List<WeatherItem>) {

        try {
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
            Log.i("TAG", "filterDaysList: ${e.message.toString()}")
        }
    }

    override fun insertWeather(
        currentWeatherResponse: CurrentWeatherResponse,
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    ) {
        viewModelScope.launch {
            try {
                val resultOne = weatherRepositoryImpl.insertCurrentWeather(currentWeatherResponse)
                val resultTwo =
                    weatherRepositoryImpl.insertFiveDaysWeather(fiveDaysWeatherForecastResponse)

                if (resultOne > 0L && resultTwo > 0L) {
//                    _message.emit(R.string.location_added_successfully)

                } else {
                    _message.emit(R.string.something_wrong_happened)
                }
            } catch (e: Exception) {
                _message.emit(R.string.something_wrong_happened)
            }
        }
    }


    override fun updateWeather(
        currentWeatherResponse: CurrentWeatherResponse,
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse,
        isConnected: Boolean
    ) {
        viewModelScope.launch {
            if(isConnected){
            try {
                val resultOne = weatherRepositoryImpl.updateCurrentWeather(currentWeatherResponse)
                val resultTwo =
                    weatherRepositoryImpl.updateFiveDaysWeather(fiveDaysWeatherForecastResponse)
                if (resultOne > 0 && resultTwo > 0) {
                    _message.emit(R.string.weather_updated_successfully)
                } else {
                    _message.emit(R.string.something_wrong_happened)
                }
            } catch (e: Exception) {
                _message.emit(R.string.something_wrong_happened)
            }
        }
        }
    }

    override fun updateSelectedWeather(
        currentWeatherResponse: CurrentWeatherResponse
    ) {
        viewModelScope.launch {
            try {
                val result = weatherRepositoryImpl.updateCurrentWeather(currentWeatherResponse)
                if (result > 0) {
                    _message.emit(R.string.weather_updated_successfully)
                } else {
                    _message.emit(R.string.something_wrong_happened)
                }
            } catch (e: Exception) {
                _message.emit(R.string.something_wrong_happened)
            }
        }
    }


    override fun getCountryName(
        longitude: Double,
        latitude: Double,
        geocoder: Geocoder,
        isConnected: Boolean
    ) {
        viewModelScope.launch {
            if (isConnected) {
                try {
                    val list = geocoder.getFromLocation(latitude, longitude, 1)
                    if (!list.isNullOrEmpty()) _countryName.emit(Response.Success(list[0]))
                } catch (e: Exception) {
                    _countryName.emit(Response.Failure(e.message.toString()))
                }
            } else {
                _countryName.emit(Response.Success(Address(Locale(""))))
            }
        }
    }

    override fun deleteWeather(
        currentWeatherResponse: CurrentWeatherResponse,
        fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse
    ) {
        viewModelScope.launch {
            try {
                val resultOne = weatherRepositoryImpl.deleteCurrentWeather(currentWeatherResponse)
                val resultTwo =
                    weatherRepositoryImpl.deleteFiveDaysWeather(fiveDaysWeatherForecastResponse)

                if (resultOne > 0 && resultTwo > 0) {
//                    _message.emit(R.string.location_deleted_successfully)
                } else {
                    _message.emit(R.string.something_wrong_happened)

                }
            } catch (e: Exception) {
                _message.emit(R.string.something_wrong_happened)
            }
        }
    }

    override fun selectFavorites() {
        viewModelScope.launch {
            try {
                selectAllFavorites()
                selectAllFiveDaysWeatherFromFavorites()
            } catch (e: Exception) {
                Log.i("TAG", "selectFavorites: ${e.message.toString()}")
            }
        }
    }

    private fun selectAllFavorites() {
        viewModelScope.launch {
            try {
                val result = weatherRepositoryImpl.selectAllFavorites()
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
                val result = weatherRepositoryImpl.selectAllFiveDaysWeatherFromFavorites()
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
            try {
                val result = weatherRepositoryImpl.selectDayWeather(
                    longitude = longitude.toBigDecimal()
                        .setScale(2, RoundingMode.DOWN).toDouble(),
                    latitude = latitude.toBigDecimal()
                        .setScale(2, RoundingMode.DOWN).toDouble()
                )
                result
                    .catch { ex -> _selectedWeather.emit(Response.Failure(ex.message.toString())) }
                    .collect { _selectedWeather.emit(Response.Success(it)) }

            } catch (e: Exception) {
                _selectedWeather.emit(Response.Failure(e.message.toString()))
            }
        }
    }

    private fun selectFiveDaysWeather(longitude: Double, latitude: Double) {
        viewModelScope.launch {
            try {
                val result =
                    weatherRepositoryImpl.selectFiveDaysWeather(
                        longitude = longitude.toBigDecimal()
                            .setScale(2, RoundingMode.DOWN).toDouble(),
                        latitude = latitude.toBigDecimal()
                            .setScale(2, RoundingMode.DOWN).toDouble()
                    )
                result
                    .catch { ex -> _selectedFiveDaysWeatherForecast.emit(Response.Failure(ex.message.toString())) }
                    .collect {
                        _selectedFiveDaysWeatherForecast.emit(Response.Success(it.list))
                        filterDaysList(it.list)
                    }
            } catch (e: Exception) {
                _selectedFiveDaysWeatherForecast.emit(Response.Failure(e.message.toString()))
            }
        }
    }

    override fun selectAllAlarms() {
        viewModelScope.launch {
            try {
                val result = weatherRepositoryImpl.selectAllAlarms()
                result
                    .catch { ex -> _alarms.emit(Response.Failure(ex.message.toString())) }
                    .collect { _alarms.emit(Response.Success(it)) }

            } catch (e: Exception) {
                _alarms.emit(Response.Failure(e.message.toString()))
            }
        }
    }

    override fun insertAlarm(alarm: AlarmModel) {
        viewModelScope.launch {
            try {
                val result = weatherRepositoryImpl.insertAlarm(alarm)
                if (result > 0L) {
                    _message.emit(R.string.alarm_set_successfully)
                } else {
                    _message.emit(R.string.something_wrong_happened)
                }
            } catch (e: Exception) {
                _message.emit(R.string.something_wrong_happened)
            }
        }
    }

    override fun deleteAlarm(locationId: Int) {
        viewModelScope.launch {
            try {
                val result = weatherRepositoryImpl.deleteAlarm(locationId)
                if (result > 0L) {
                    _message.emit(R.string.alarm_reset_successfully)
                } else {
                    _message.emit(R.string.something_wrong_happened)
                }
            } catch (e: Exception) {
                _message.emit(R.string.something_wrong_happened)
            }
        }
    }


    override fun updateAlarm(alarm: AlarmModel) {
        viewModelScope.launch {
            try {
                val result = weatherRepositoryImpl.updateAlarm(alarm)
                if (result > 0L) {
                    _message.emit(R.string.alarm_updated_successfully)
                } else {
                    _message.emit(R.string.something_wrong_happened)
                }
            } catch (e: Exception) {
                _message.emit(R.string.something_wrong_happened)
            }
        }
    }

    override fun calculateDelay(
        targetYear: Int,
        targetMonth: Int,
        targetDay: Int,
        targetHour: Int,
        targetMinute: Int
    ): Long {
        val now = java.util.Calendar.getInstance()

        val targetTime = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, targetYear)
            set(java.util.Calendar.MONTH, targetMonth - 1)
            set(java.util.Calendar.DAY_OF_MONTH, targetDay)
            set(java.util.Calendar.HOUR_OF_DAY, targetHour)
            set(java.util.Calendar.MINUTE, targetMinute)
            set(java.util.Calendar.SECOND, 0)
        }

        val delay = targetTime.timeInMillis - now.timeInMillis

        return if (delay > 0) delay else 0L
    }

   override fun onSearchTextChange(text: String) {
        viewModelScope.launch {
            _searchText.emit(text)
        }


    }

}


class FavoriteViewModelFactory(
    private val weatherRepositoryImpl: WeatherRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteAndAlarmSharedViewModelImpl(weatherRepositoryImpl) as T
    }
}

