package com.example.weatherapp.data.local

import android.content.Context
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
        get() = prefs.getString(Strings.LOCATION_STATE, "GPS") ?: ""


}