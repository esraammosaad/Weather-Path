package com.example.weatherapp.data.local

import android.content.Context
import com.example.weatherapp.R
import com.example.weatherapp.settings.view.getDefaultSystemLang
import com.example.weatherapp.utilis.Strings

class LocalStorageDataSource private constructor(context: Context) {
    private var prefs =
        context.getSharedPreferences(Strings.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    private var editor = prefs.edit()

    companion object {
        @Volatile
        private var instance: LocalStorageDataSource? = null
        fun getInstance(context: Context): LocalStorageDataSource {
            return instance ?: synchronized(this) {
                val temp = LocalStorageDataSource(context)
                instance = temp
                temp
            }

        }
    }

    fun saveGetStartedStateState() {
        editor.putBoolean(Strings.SEEN_GET_STARTED, true)
        editor.apply()
    }

    val getStartedState: Boolean
        get() = prefs.getBoolean(Strings.SEEN_GET_STARTED, false)

    fun saveLocationState(locationState: Int) {
        editor.putInt(Strings.LOCATION_STATE, locationState)
        editor.apply()
    }
    val getLocationState: Int
        get() = prefs.getInt(Strings.LOCATION_STATE, R.string.gps)

    fun saveLanguageCode(langCode: String) {
        editor.putString(Strings.LANG_CODE, langCode)
        editor.apply()
    }
    val getLanguageCode: String
        get() = prefs.getString(Strings.LANG_CODE, getDefaultSystemLang()) ?: getDefaultSystemLang()

    fun saveTempUnit(tempUnit: String) {
        editor.putString(Strings.TEMP_UNIT, tempUnit)
        editor.apply()
    }
    val getTempUnit: String
        get() = prefs.getString(Strings.TEMP_UNIT, Strings.CELSIUS) ?: Strings.CELSIUS


    fun saveTempSymbol(tempSymbol: Int) {
        editor.putInt(Strings.TEMP_SYMBOL, tempSymbol)
        editor.apply()
    }
    val getTempSymbol: Int
        get() = prefs.getInt(Strings.TEMP_SYMBOL, R.string.celsius)

    fun saveWindUnit(windUnit: Int) {
        editor.putInt(Strings.WIND_UNIT, windUnit)
        editor.apply()
    }
    val getWindUnit: Int
        get() = prefs.getInt(Strings.WIND_UNIT, R.string.meter_sec)

    fun savePickedLong(longitude: Double) {
        editor.putFloat(Strings.PICKED_LONG, longitude.toFloat())
        editor.apply()
    }
    val getPickedLong: Double
        get() = prefs.getFloat(Strings.PICKED_LONG, 0.0f).toDouble()

    fun savePickedLat(latitude: Double) {
        editor.putFloat(Strings.PICKED_LAT, latitude.toFloat())
        editor.apply()
    }
    val getPickedLat: Double
        get() = prefs.getFloat(Strings.PICKED_LAT, 0.0f).toDouble()

    fun saveIsMap(isMap: Boolean) {
        editor.putBoolean("IS_Map", isMap)
        editor.apply()
    }
    val getIsMap: Boolean
        get() = prefs.getBoolean("IS_Map", false)

}