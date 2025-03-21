package com.example.weatherapp.data.remote

import com.example.weatherapp.utilis.Strings
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {

    private val retrofit: Retrofit = Retrofit
        .Builder()
        .baseUrl(Strings.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()

    var apiService: ApiService = retrofit.create(ApiService::class.java)

}
