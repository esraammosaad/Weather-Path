package com.example.weatherapp.data.local

import android.content.Context
import com.example.weatherapp.R
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

    fun saveLocationState(locationState: String) {
        editor.putString(Strings.LOCATION_STATE, locationState)
        editor.apply()
    }
    val getLocationState: String
        get() = prefs.getString(Strings.LOCATION_STATE, "GPS") ?: "GPS"

    fun saveLanguageCode(langCode: String) {
        editor.putString(Strings.LANG_CODE, langCode)
        editor.apply()
    }
    val getLanguageCode: String
        get() = prefs.getString(Strings.LANG_CODE, "en") ?: "en"

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

}