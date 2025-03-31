package com.example.weatherapp.utilis

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import java.util.Locale


object LocalizationHelper {


    fun updateLocale(context: Context, languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)

        } else {

            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val resources = context.resources
            val config = resources.configuration
            config.setLocale(locale)

            context.createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)

        }


    }

}
