package com.example.weatherapp.home_settings_feature.view_model

import android.location.Address
import android.location.Geocoder
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.data.model.Response
import com.example.weatherapp.data.model.current_weather.Clouds
import com.example.weatherapp.data.model.current_weather.Coord
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.current_weather.Main
import com.example.weatherapp.data.model.current_weather.Sys
import com.example.weatherapp.data.model.current_weather.Wind
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.utilis.Strings
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.mockk.coEvery
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class HomeAndSettingsSharedViewModelImplTest {

    private lateinit var repository: WeatherRepository
    private lateinit var homeAndSettingsSharedViewModelImpl: HomeAndSettingsSharedViewModel
    private lateinit var currentWeatherResponse: CurrentWeatherResponse
    private lateinit var geocoder: Geocoder
    private val latitude = 0.0
    private val longitude = 0.0
    private var isConnected = true
    private val languageCode = "en"
    private val tempUnit = Strings.CELSIUS

    @get:Rule
    val myRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        currentWeatherResponse = currentWeatherResponse()
        geocoder = Geocoder(ApplicationProvider.getApplicationContext())
        repository = mockk(relaxed = true)
        coEvery {
            repository.getCurrentWeather(latitude, longitude, languageCode, tempUnit)
        } returns flowOf( currentWeatherResponse)
        coEvery { repository.selectDayWeather(latitude, longitude) } returns flowOf(
            currentWeatherResponse
        )
        coEvery {
            repository.getFiveDaysWeatherForecast(
                latitude,
                longitude,
                languageCode,
                tempUnit
            )
        } returns flowOf(listOf( weatherItem()))
        coEvery { repository.selectFiveDaysWeather(latitude, longitude) } returns flowOf(
            fiveDaysForecastResponse()
        )

        coEvery { repository.getCountryName(latitude, longitude, geocoder) } returns mutableListOf(
            Address(
                Locale("en")
            )
        )
        homeAndSettingsSharedViewModelImpl = HomeAndSettingsSharedViewModelImpl(repository)
        Dispatchers.setMain(StandardTestDispatcher())

    }

    private fun currentWeatherResponse() = CurrentWeatherResponse(
        base = "90",
        clouds = clouds(),
        cod = 10,
        coord = Coord(lat = latitude, lon = longitude),
        dt = 0,
        id = 0,
        main = main(),
        name = "",
        sys = Sys(country = "", sunrise = 0, sunset = 0),
        timezone = 0,
        visibility = 0,
        weather = listOf(),
        wind = wind(),
        latitude = latitude,
        longitude = longitude,
        countryName = "",
        cityName = ""
    )

    private fun main() = Main(
        feels_like = 0.0,
        grnd_level = 0,
        humidity = 0,
        pressure = 0,
        sea_level = 0,
        temp = 0.0,
        temp_max = 0.0,
        temp_min = 0.0
    )

    private fun fiveDaysForecastResponse() = FiveDaysWeatherForecastResponse(
        list = listOf(
            weatherItem()
        ),
        latitude = latitude,
        longitude = longitude
    )

    private fun weatherItem() = WeatherItem(
        clouds = clouds(),
        dt = 0,
        dt_txt = "",
        main = main(),
        pop = 0.0,
        visibility = 0,
        weather = listOf(),
        wind = wind()
    )

    private fun clouds() = Clouds(all = 0)

    private fun wind() = Wind(deg = 0, gust = 0.0, speed = 0.0)

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getCurrentWeather_latitudeZeroAndLongitude0WithConnectedInternet() = runTest {

        //Given
        val values = mutableListOf<Response>()
        backgroundScope.launch((UnconfinedTestDispatcher(testScheduler))) {
            homeAndSettingsSharedViewModelImpl
                .currentWeather
                .toList(
                    values
                )
        }

        //When
        homeAndSettingsSharedViewModelImpl.getCurrentWeather(
            latitude = latitude,
            longitude = longitude,
            isConnected = isConnected,
            languageCode = languageCode,
            tempUnit = tempUnit
        )
        advanceUntilIdle()

        //Then
        assertThat(values.first(), `is`(Response.Loading))
        assertThat(values.last(), `is`(instanceOf(Response.Success::class.java)))
        val result = values.last() as Response.Success<CurrentWeatherResponse>
        assertThat(result.result?.longitude, `is`(0.0))
        assertThat(result.result?.latitude, `is`(0.0))
        assertThat(result.result, `is`(currentWeatherResponse()))

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getCurrentWeather_latitudeZeroAndLongitude0WithNoConnectedInternet() = runTest {

        //Given
        isConnected = false
        val values = mutableListOf<Response>()
        backgroundScope.launch((UnconfinedTestDispatcher(testScheduler))) {
            homeAndSettingsSharedViewModelImpl
                .currentWeather
                .toList(
                    values
                )
        }

        //When
        homeAndSettingsSharedViewModelImpl.getCurrentWeather(
            latitude = latitude,
            longitude = longitude,
            isConnected = isConnected,
            languageCode = languageCode,
            tempUnit = tempUnit
        )
        advanceUntilIdle()

        //Then
        assertThat(values.first(), `is`(Response.Loading))
        assertThat(values.last(), `is`(instanceOf(Response.Success::class.java)))
        val result = values.last() as Response.Success<CurrentWeatherResponse>
        assertThat(result.result?.longitude, `is`(0.0))
        assertThat(result.result?.latitude, `is`(0.0))
        assertThat(result.result, `is`(currentWeatherResponse()))

    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getFiveDaysWeatherForecast_latitudeZeroAndLongitude0WithConnectedInternet() = runTest {

        //Given
        val values = mutableListOf<Response>()
        backgroundScope.launch((UnconfinedTestDispatcher(testScheduler))) {
            homeAndSettingsSharedViewModelImpl
                .fiveDaysWeatherForecast
                .toList(
                    values
                )
        }

        //When
        homeAndSettingsSharedViewModelImpl.getFiveDaysWeatherForecast(
            latitude = latitude,
            longitude = longitude,
            isConnected = isConnected,
            languageCode = languageCode,
            tempUnit = tempUnit
        )
        advanceUntilIdle()

        //Then
        assertThat(values.first(), `is`(Response.Loading))
        assertThat(values.last(), `is`(instanceOf(Response.Success::class.java)))
        val result = values.last() as Response.Success<List<WeatherItem>>
        assertTrue(result.result?.isNotEmpty() ?: false)
        assertThat(result.result?.first(), not(nullValue()))
        assertThat(result.result?.first(), `is`(weatherItem()))

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getFiveDaysWeatherForecast_latitudeZeroAndLongitude0WithNoConnectedInternet() = runTest {

        //Given
        isConnected = false
        val values = mutableListOf<Response>()
        backgroundScope.launch((UnconfinedTestDispatcher(testScheduler))) {
            homeAndSettingsSharedViewModelImpl
                .fiveDaysWeatherForecast
                .toList(
                    values
                )
        }

        //When
        homeAndSettingsSharedViewModelImpl.getFiveDaysWeatherForecast(
            latitude = latitude,
            longitude = longitude,
            isConnected = isConnected,
            languageCode = languageCode,
            tempUnit = tempUnit
        )
        advanceUntilIdle()

        //Then
        assertThat(values.first(), `is`(Response.Loading))
        assertThat(values.last(), `is`(instanceOf(Response.Success::class.java)))
        val result = values.last() as Response.Success<List<WeatherItem>>
        assertTrue(result.result?.isNotEmpty() ?: false)
        assertThat(result.result?.first(), not(nullValue()))
        assertThat(result.result?.first(), `is`(weatherItem()))

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getCountryName_latitudeZeroAndLongitude0WithConnectedInternet() = runTest {

        //Given
        val values = mutableListOf<Response>()
        backgroundScope.launch((UnconfinedTestDispatcher(testScheduler))) {
            homeAndSettingsSharedViewModelImpl
                .countryName
                .toList(
                    values
                )
        }

        //When
        homeAndSettingsSharedViewModelImpl.getCountryName(
            latitude = latitude,
            longitude = longitude,
            isConnected = isConnected,
            geocoder =geocoder,
        )
        advanceUntilIdle()

        //Then
        assertThat(values.first(), `is`(Response.Loading))
        assertThat(values.last(), `is`(instanceOf(Response.Success::class.java)))
        val result = values.last() as Response.Success<Address>
        assertThat(result.result, not(nullValue()))

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getCountryName_latitudeZeroAndLongitude0WithNoConnectedInternet() = runTest {

        //Given
        isConnected=false
        val values = mutableListOf<Response>()
        backgroundScope.launch((UnconfinedTestDispatcher(testScheduler))) {
            homeAndSettingsSharedViewModelImpl
                .countryName
                .toList(
                    values
                )
        }

        //When
        homeAndSettingsSharedViewModelImpl.getCountryName(
            latitude = latitude,
            longitude = longitude,
            isConnected = isConnected,
            geocoder =geocoder,
        )
        advanceUntilIdle()

        //Then
        assertThat(values.first(), `is`(Response.Loading))
        assertThat(values.last(), `is`(instanceOf(Response.Success::class.java)))
        val result = values.last() as Response.Success<Address>
        assertThat(result.result, not(nullValue()))
    }
}
