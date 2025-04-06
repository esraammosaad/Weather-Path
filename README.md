## Weather Path - Follow the weather wherever
#### Weather Path is an Android mobile application built with modern development tools and clean architecture (MVVM). It provides accurate weather forecasts for your current location or any selected place. You can also manage favorite locations, receive scheduled weather alerts, and personalize settings like units and language.
 
## Features
### Home Screen
##### Displays:
- Current temperature, date, and time
- Humidity, wind speed, pressure, and cloud coverage
- Weather description with icon (e.g., clear sky, light rain)
- 3-Hourly forecast for the current day
- Daily forecast for the next 5 days
  
### Favorites
- Add favorite locations using:
- Google Map marker
- Places API autocomplete search
- View saved locations and their forecast
- Remove favorites easily

### Weather Alerts
- Set weather alerts for specific locations and times
- Choose between notification or alarm
- Configure alert duration and toggle on/off ,edit and remove

### Settings
- Location: Use GPS or select manually from the map
- Units: Temperature (Kelvin, Celsius, Fahrenheit)
- Wind Speed (m/s or mph)
- Language: Arabic or English or System Default (localization supported)

### Technologies & Tools Used
- Jetpack Compose (modern UI toolkit)
- Kotlin Coroutines & Flow / StateFlow (asynchronous & reactive programming)
- SharedPreferences (user settings persistence)
- Room (local database for favorites and alerts)
- Retrofit (API integration with OpenWeatherMap)
- Google Maps API & Places API (map selection and auto-complete search)
- MVVM Architecture with ViewModel
- Localization (English & Arabic & System Default)
- Unit Testing
- WorkManager (background weather updates)
-AlarmManager, BroadcastReceiver, Notifications (customized weather alerts)

## Demo

https://github.com/user-attachments/assets/f1266561-2f89-4680-b729-c26ec56b5c8f


