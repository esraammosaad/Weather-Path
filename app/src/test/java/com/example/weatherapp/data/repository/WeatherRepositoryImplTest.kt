package com.example.weatherapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.FakeWeatherLocalDataSource
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.model.current_weather.Clouds
import com.example.weatherapp.data.model.current_weather.Coord
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.current_weather.Main
import com.example.weatherapp.data.model.current_weather.Sys
import com.example.weatherapp.data.model.current_weather.Wind
import com.example.weatherapp.data.model.five_days_weather_forecast.FiveDaysWeatherForecastResponse
import com.example.weatherapp.data.model.five_days_weather_forecast.WeatherItem
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.utilis.Strings
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherRepositoryImplTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var weatherLocalDataSource: WeatherLocalDataSource
    private lateinit var weatherRemoteDataSource: WeatherRemoteDataSource
    private val latitude = 0.0
    private val longitude = 0.0
    private val languageCode = "en"
    private val tempUnit = Strings.CELSIUS

    private fun currentWeatherResponse(id: Int = 0) = CurrentWeatherResponse(
        base = "90",
        clouds = clouds(),
        cod = 10,
        coord = Coord(lat = 0.0, lon = 0.0),
        dt = 0,
        id = id,
        main = main(),
        name = "",
        sys = Sys(country = "", sunrise = 0, sunset = 0),
        timezone = 0,
        visibility = 0,
        weather = listOf(),
        wind = wind(),
        latitude = 0.0,
        longitude = 0.0,
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

    private fun clouds() = Clouds(all = 0)

    private fun wind() = Wind(deg = 0, gust = 0.0, speed = 0.0)

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

    @get:Rule
    val myRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        weatherLocalDataSource = FakeWeatherLocalDataSource()
        weatherRemoteDataSource = mockk(relaxed = true)
        coEvery {
            weatherRemoteDataSource.getCurrentWeather(
                latitude = latitude, longitude = longitude,
                languageCode = languageCode,
                tempUnit = tempUnit,
            )
        } returns currentWeatherResponse()

        coEvery {
            weatherRemoteDataSource.getFiveDaysWeatherForecast(
                latitude = latitude, longitude = longitude,
                languageCode = languageCode,
                tempUnit = tempUnit,
            )
        } returns flowOf(weatherItem())

        weatherRepository = WeatherRepositoryImpl(
            weatherLocalDataSourceImpl = weatherLocalDataSource,
            weatherRemoteDataSourceImpl = weatherRemoteDataSource
        )
        Dispatchers.setMain(StandardTestDispatcher())
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun insertCurrentWeather_currentWeatherResponse_1L() = runTest {
        //Given

        //When
        val result = weatherRepository.insertCurrentWeather(currentWeatherResponse(4))

        //Then
        assertThat(result, `is`(1L))
    }

    @Test
    fun insertCurrentWeather_currentWeatherResponse_0L() = runTest {
        //Given

        //When
        val result = weatherRepository.insertCurrentWeather(currentWeatherResponse(0))

        //Then
        assertThat(result, `is`(0L))
    }

    @Test
    fun deleteCurrentWeather_1() = runTest {
        //Given

        //When
        val result = weatherRepository.deleteCurrentWeather(currentWeatherResponse(0))

        //Then
        assertThat(result, `is`(1))
    }

    @Test
    fun deleteCurrentWeather_0() = runTest {
        //Given

        //When
        val result = weatherRepository.deleteCurrentWeather(currentWeatherResponse(4))

        //Then
        assertThat(result, `is`(0))
    }

    @Test
    fun selectAllFavorites_listOfWeatherItems() = runTest {
        //Given
        val values = mutableListOf<List<CurrentWeatherResponse>>()


        //When
        weatherRepository
            .selectAllFavorites()
            .toList(
                values
            )


        //Then
        assertThat(values.first().size, `is`(3))
        assertThat(values.first()[0].id, `is`(0))
        assertThat(values.first()[1].id, `is`(1))
        assertThat(values.first()[2].id, `is`(2))
    }


    @Test
    fun getCurrentWeather_currentWeatherResponse() = runTest {
        //Given


        //When
        val result = weatherRepository
            .getCurrentWeather(
                longitude = longitude,
                latitude = latitude,
                languageCode = languageCode,
                tempUnit = tempUnit
            )


        //Then
        assertThat(result, `is`(currentWeatherResponse()))
    }

    @Test
    fun getFiveDaysWeatherForecast_weatherItems() = runTest {
        //Given
        val values = mutableListOf<WeatherItem>()


        //When
        weatherRepository
            .getFiveDaysWeatherForecast(
                longitude = longitude,
                latitude = latitude,
                languageCode = languageCode,
                tempUnit = tempUnit
            ).toList(
                values
            )


        //Then
        assertThat(values.first(), `is`(weatherItem()))
    }

}