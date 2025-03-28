package com.example.weatherapp.internet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class InternetConnectivityViewModel(internetObserver: InternetObserver): ViewModel() {

    val isConnected = internetObserver.isConnected.stateIn( //cold flow to hot flow
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L), //in background will cancel observation after 5000 Millis
        false
    )
}

class InternetConnectivityViewModelFactory(private val internetObserver: InternetObserver):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InternetConnectivityViewModel(internetObserver = internetObserver) as T
    }
}