package com.example.weatherapp.utilis.internet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InternetConnectivityViewModel(val internetObserver: InternetObserver) : ViewModel() {

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    fun getInternetConnectivity() {
        viewModelScope.launch {
            internetObserver.isConnected.collect {
                Log.i("TAG", "getInternetConnectivity: $it")
                _isConnected.emit(it)
                Log.i("TAG", "getInternetConnectivity:111 ${_isConnected.value}")
                Log.i("TAG", "getInternetConnectivity:222 ${isConnected.value}")

            }
        }
    }

//    val isConnected = internetObserver.isConnected.stateIn( //cold flow to hot flow
//        viewModelScope,
//        SharingStarted.WhileSubscribed(5000L), //in background will cancel observation after 5000 Millis
//        true
//    )


}

class InternetConnectivityViewModelFactory(private val internetObserver: InternetObserver) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InternetConnectivityViewModel(internetObserver = internetObserver) as T
    }
}