package com.example.weatherapp.home.view_model

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.repository.WeatherRepository
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


class HomeViewModelImpl(
    private val weatherRepositoryImpl: WeatherRepository,
) : ViewModel(), HomeViewModel {

    private var _currentWeather: MutableStateFlow<Response> = MutableStateFlow(Response.Loading)
    override var currentWeather = _currentWeather.asStateFlow()

    private var _fiveDaysWeatherForecast: MutableStateFlow<Response> =
        MutableStateFlow(Response.Loading)

    override var fiveDaysWeatherForecast = _fiveDaysWeatherForecast.asStateFlow()

    private var _message: MutableStateFlow<Int> = MutableStateFlow(R.string.loading)
    override var message = _message.asStateFlow()

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


    override fun getCurrentWeather(
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
                    _currentWeather.emit(Response.Success(result))
                    _message.emit(R.string.success)
                } catch (e: Exception) {

                    _message.emit(R.string.something_wrong_happened)
                    selectDayWeather(longitude = longitude, latitude = latitude)
                    Log.i("TAG", "getCurrentWeather: ${e.message.toString()}")
                }
            } else {
                selectDayWeather(longitude = longitude, latitude = latitude)
            }
        }
    }

    override fun getFiveDaysWeatherForecast(
        latitude: Double,
        longitude: Double,
        isConnected: Boolean,
        languageCode: String,
        tempUnit: String
    ) {
        viewModelScope.launch {
            if (isConnected) {
                try {
                    val result =
                        weatherRepositoryImpl.getFiveDaysWeatherForecast(
                            latitude = latitude,
                            longitude = longitude,
                            languageCode = languageCode,
                            tempUnit = tempUnit
                        )
                    _fiveDaysWeatherForecast
                        .emit(
                            Response.Success(
                                result
                                    .catch { ex ->
                                        _message.emit(R.string.something_wrong_happened)
                                        Log.i(
                                            "TAG",
                                            "getFiveDaysWeatherForecast: ${ex.message.toString()}"
                                        )
                                        selectFiveDaysWeather(
                                            longitude = longitude,
                                            latitude = latitude
                                        )
                                    }.toList()
                            )
                        )
                    filterDaysList(result)
                } catch (e: Exception) {
                    _message.emit(R.string.something_wrong_happened)
                    selectFiveDaysWeather(longitude = longitude, latitude = latitude)
                    Log.i("TAG", "getFiveDaysWeatherForecast: ${e.message.toString()}")
                }
            } else {
                selectFiveDaysWeather(longitude = longitude, latitude = latitude)
            }
        }
    }

    private suspend fun filterDaysList(result: Flow<WeatherItem>) {
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
            _message.emit(R.string.something_wrong_happened)
        }
    }


    private fun insertCurrentWeather(currentWeatherResponse: CurrentWeatherResponse) {
        viewModelScope.launch {
            try {
                val result = weatherRepositoryImpl.insertCurrentWeather(currentWeatherResponse)
                if (result > 0L) {
                    _message.emit(R.string.current_weather_inserted_successfully)
                } else {
                    _message.emit(R.string.something_wrong_happened)
                }
            } catch (e: Exception) {
                _message.emit(R.string.something_wrong_happened)
            }
        }

    }

    private fun insertFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse) {
        viewModelScope.launch {
            try {
                val result =
                    weatherRepositoryImpl.insertFiveDaysWeather(fiveDaysWeatherForecastResponse)
                if (result > 0L) {
                    _message.emit(R.string.five_days_inserted_successfully)
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
            try {
                if (isConnected) {
                    val list =
                        weatherRepositoryImpl.getCountryName(
                            longitude = longitude,
                            latitude = latitude,
                            geocoder = geocoder
                        )
                    if (!list.isNullOrEmpty())
                        _countryName.emit(Response.Success(list[0]))
                } else {
                    _countryName.emit(Response.Success(Address(Locale("ar"))))
                }
            } catch (e: Exception) {
                _countryName.emit(Response.Failure(e.message.toString()))
                _message.emit(R.string.something_wrong_happened)
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
                result.catch { ex ->
                    _currentWeather.emit(Response.Failure(ex.message.toString()))
                }.collect {
                    _currentWeather.emit(Response.Success(it))
                }
            } catch (e: Exception) {
                _currentWeather.emit(Response.Failure(e.message.toString()))

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
                result.catch { ex ->
                    _fiveDaysWeatherForecast.emit(Response.Failure(ex.message.toString()))
                }.collect {
                    _fiveDaysWeatherForecast.emit(Response.Success(it.list))
                    filterDaysList(it.list.asFlow())
                }
            } catch (e: Exception) {
                _fiveDaysWeatherForecast.emit(Response.Failure(e.message.toString()))
            }
        }
    }

    override fun getWeatherFromApi(
        locationState: Location, geocoder: Geocoder, isConnected: Boolean, languageCode: String,
        tempUnit: String
    ) {
        try {
            getCurrentWeather(
                latitude = locationState.latitude,
                longitude = locationState.longitude,
                isConnected = isConnected,
                languageCode = languageCode,
                tempUnit = tempUnit
            )
            getCountryName(
                longitude = locationState.longitude,
                latitude = locationState.latitude,
                geocoder = geocoder,
                isConnected
            )
            getFiveDaysWeatherForecast(
                latitude = locationState.latitude,
                longitude = locationState.longitude,
                isConnected = isConnected,
                languageCode = languageCode,
                tempUnit = tempUnit
            )
        } catch (e: Exception) {
            Log.i("TAG", "getWeatherFromApi: ${e.message.toString()}")
        }


    }

    override fun insertFiveDaysForecast(
        fiveDaysWeatherForecast: List<WeatherItem>?,
        longitude: Double,
        latitude: Double
    ) {
        try {
            fiveDaysWeatherForecast?.let { forecastItems ->
                FiveDaysWeatherForecastResponse(
                    list = forecastItems,
                    latitude = latitude.toBigDecimal()
                        .setScale(2, RoundingMode.DOWN).toDouble(),
                    longitude = longitude.toBigDecimal()
                        .setScale(2, RoundingMode.DOWN).toDouble()
                )
            }?.let { forecastResponse ->
                insertFiveDaysWeather(
                    forecastResponse
                )
            }
        } catch (e: Exception) {
            Log.i("TAG", "insertFiveDaysForecast: ${e.message.toString()}")
        }
    }

    override fun insertCurrentWeather(
        currentWeather: CurrentWeatherResponse?,
        countryName: Address?,
        longitude: Double,
        latitude: Double
    ) {
        try {
            currentWeather?.let { weather ->
                weather.latitude =
                    latitude.toBigDecimal().setScale(2, RoundingMode.DOWN)
                        .toDouble()
                weather.longitude =
                    longitude.toBigDecimal().setScale(2, RoundingMode.DOWN)
                        .toDouble()
                weather.countryName =
                    countryName?.countryName ?: weather.sys.country
                weather.cityName = countryName?.locality ?: weather.name
                insertCurrentWeather(
                    weather
                )
            }
        } catch (e: Exception) {
            Log.i("TAG", "insertCurrentWeather: ${e.message.toString()}")
        }
    }


}

class HomeViewModelFactory(
    private val weatherRepositoryImpl: WeatherRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModelImpl(weatherRepositoryImpl) as T
    }
}