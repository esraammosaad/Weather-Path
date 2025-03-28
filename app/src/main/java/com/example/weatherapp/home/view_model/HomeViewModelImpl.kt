package com.example.weatherapp.home.view_model

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.log


class HomeViewModelImpl(
    private val repository: Repository,
) : ViewModel() {

    private var _currentWeather: MutableStateFlow<Response> = MutableStateFlow(Response.Loading)
    var currentWeather = _currentWeather.asStateFlow()

    private var _fiveDaysWeatherForecast: MutableStateFlow<Response> =
        MutableStateFlow(Response.Loading)

    var fiveDaysWeatherForecast = _fiveDaysWeatherForecast.asStateFlow()

    private var _message: MutableLiveData<String> = MutableLiveData()
    var message: LiveData<String> = _message

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


    private fun getCurrentWeather(latitude: Double, longitude: Double, isConnected: Boolean) {

        Log.i("TAG", "getCurrentWeather: ${isConnected}")
        viewModelScope.launch {

            if (isConnected) {
                try {
                    val result = repository.getCurrentWeather(
                        latitude = latitude,
                        longitude = longitude
                    )
                    _currentWeather.emit(Response.Success(result))
                    _message.postValue("Success")
                    Log.i("TAG", "getSelectedWeather: nnnnnnnnnnnn")

//                    _message.postValue("something wrong happen please try again!!")

                } catch (e: Exception) {
                    _message.postValue(e.message.toString())
                    selectDayWeather(longitude = longitude, latitude = latitude)
                    Log.i("TAG", "getSelectedWeather: nnnnnnnnnnnn")
                }
            } else {

                Log.i("TAG", "getCurrentWeather: false")
                selectDayWeather(longitude = longitude, latitude = latitude)

            }

        }


    }

    private fun getFiveDaysWeatherForecast(
        latitude: Double,
        longitude: Double,
        isConnected: Boolean
    ) {
        viewModelScope.launch {
            if (isConnected) {
                try {
                    val result =
                        repository.getFiveDaysWeatherForecast(
                            latitude = latitude,
                            longitude = longitude
                        )

                    _fiveDaysWeatherForecast.emit(Response.Success(result.catch {ex->
                        _message.postValue(ex.message.toString())
                        selectFiveDaysWeather(longitude = longitude, latitude = latitude)

                    }.toList()))

                    filterDaysList(result)


                } catch (e: Exception) {
                    _message.postValue(e.message.toString())
                    selectFiveDaysWeather(longitude = longitude, latitude = latitude)


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


    private fun insertCurrentWeather(currentWeatherResponse: CurrentWeatherResponse) {
        viewModelScope.launch {
            repository.insertCurrentWeather(currentWeatherResponse)
        }

    }

    private fun insertFiveDaysWeather(fiveDaysWeatherForecastResponse: FiveDaysWeatherForecastResponse) {
        viewModelScope.launch {
            repository.insertFiveDaysWeather(fiveDaysWeatherForecastResponse)
        }

    }

    private fun getCountryName(
        longitude: Double,
        latitude: Double,
        geocoder: Geocoder,
        isConnected: Boolean
    ) {
        viewModelScope.launch {

            if (isConnected) {
                val list =
                    geocoder.getFromLocation(latitude, longitude, 1)
                if (!list.isNullOrEmpty())
                    _countryName.emit(Response.Success(list[0]))
            } else {

                _countryName.emit(Response.Success(Address(Locale("ar"))))

            }
        }
    }

    private fun selectDayWeather(longitude: Double, latitude: Double) {

        Log.i("TAG", "selectDayWeather: hii $longitude")
        viewModelScope.launch {
            Log.i("TAG", "selectDayWeather: heeeeeee")
            val result = repository.selectDayWeather(
                longitude = longitude.toBigDecimal()
                    .setScale(2, RoundingMode.DOWN).toDouble(), latitude = latitude.toBigDecimal()
                    .setScale(2, RoundingMode.DOWN).toDouble()
            )
            Log.i("TAG", "selectDayWeather:jjjjj $result")
            result.collect {
                _currentWeather.emit(Response.Success(it))
                Log.i("TAG", "selectDayWeather: $it")
            }
            Log.i("TAG", "==== selectDayWeather: $result")
            Log.i("TAG", "selectDayWeather: $latitude , $longitude")
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
                _fiveDaysWeatherForecast.emit(Response.Success(it.list))
                filterDaysList(it.list.asFlow())
            }
            Log.i("TAG", "==== selectDayWeather: $result")
            Log.i("TAG", "selectDayWeather: $latitude , $longitude")
        }
    }

    fun getWeatherFromApi(locationState: Location, geocoder: Geocoder, isConnected: Boolean) {
        Log.i("TAG", "getWeatherFromApi: ---------------")
        getCurrentWeather(
            latitude = locationState.latitude,
            longitude = locationState.longitude,
            isConnected
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
            isConnected
        )
    }

    fun insertFiveDaysForecast(
        fiveDaysWeatherForecast: List<WeatherItem>?,
        locationState: Location
    ) {
        fiveDaysWeatherForecast?.let { it1 ->
            FiveDaysWeatherForecastResponse(
                list = it1,
                latitude = locationState.latitude.toBigDecimal()
                    .setScale(2, RoundingMode.DOWN).toDouble(),
                longitude = locationState.longitude.toBigDecimal()
                    .setScale(2, RoundingMode.DOWN).toDouble()
            )
        }?.let { it2 ->
            insertFiveDaysWeather(
                it2

            )
        }
    }

    fun insertCurrentWeather(
        currentWeather: CurrentWeatherResponse?,
        countryName: Address?,
        locationState: Location

    ) {
        currentWeather?.let { currentWeather ->
            currentWeather.latitude =
                locationState.latitude.toBigDecimal().setScale(2, RoundingMode.DOWN)
                    .toDouble()
            currentWeather.longitude =
                locationState.longitude.toBigDecimal().setScale(2, RoundingMode.DOWN)
                    .toDouble()
            currentWeather.countryName = countryName?.countryName ?: ""
            currentWeather.cityName = countryName?.locality ?: ""
            insertCurrentWeather(
                currentWeather
            )
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