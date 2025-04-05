package com.example.weatherapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapp.data.model.current_weather.Clouds
import com.example.weatherapp.data.model.current_weather.Coord
import com.example.weatherapp.data.model.current_weather.CurrentWeatherResponse
import com.example.weatherapp.data.model.current_weather.Main
import com.example.weatherapp.data.model.current_weather.Sys
import com.example.weatherapp.data.model.current_weather.Wind
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDaoTest {


    private lateinit var weatherDatabase: WeatherDatabase
    private lateinit var weatherDao: WeatherDao
    private lateinit var currentWeatherResponse: CurrentWeatherResponse
    private lateinit var updatedCurrentWeatherResponse: CurrentWeatherResponse


    private val latitude = 0.000

    private val longitude = 0.000


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


    @Before
    fun setup() {

        weatherDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider
                .getApplicationContext(), WeatherDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        weatherDao = weatherDatabase.getDao()
        currentWeatherResponse = currentWeatherResponse()
        updatedCurrentWeatherResponse = updatedCurrentWeatherResponse()

    }

    @After
    fun tearDown() {
        weatherDatabase.close()
    }

    private fun updatedCurrentWeatherResponse() = CurrentWeatherResponse(
        base = "100",
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


    @Test
    fun insertCurrentWeather_1L() = runTest {

        //Given

        // When
        val res = weatherDao.insertCurrentWeather(currentWeatherResponse)

        //Then
        assertThat(res, `is`(1L))

    }

    @Test
    fun insertCurrentWeatherAndThenDeleteAndThenSelect() = runTest {

        //Given
        weatherDao.insertCurrentWeather(currentWeatherResponse)
        val result = weatherDao.deleteCurrentWeather(currentWeatherResponse)


        // When
        val item = weatherDao.selectAllFavorites().first()

        //Then
        assertThat(result, `is`(1))
        assertThat(item.size, `is`(0))

    }

    @Test
    fun insertCurrentWeatherAndThenSelectTheSameItem() = runTest {

        //Given
        weatherDao.insertCurrentWeather(currentWeatherResponse)

        // When
        val item = weatherDao.selectDayWeather(latitude, longitude).first()

        //Then
        assertThat(item, `is`(currentWeatherResponse))

    }

    @Test
    fun updateCurrentWeatherAndThenSelectTheUpdatedItem() = runTest {

        //Given
        weatherDao.insertCurrentWeather(currentWeatherResponse)
        weatherDao.updateCurrentWeather(updatedCurrentWeatherResponse)

        // When
        val item = weatherDao.selectDayWeather(latitude, longitude).first()

        //Then
        assertThat(item, `is`(updatedCurrentWeatherResponse))

    }

    @Test
    fun insertItemAndThenSelectAllItems() = runTest {

        //Given
        weatherDao.insertCurrentWeather(currentWeatherResponse)

        // When
        val item = weatherDao.selectAllFavorites().first()

        //Then
        assertThat(item.first(), `is`(currentWeatherResponse))

    }


}