package com.example.smartfood.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://26.54.240.231:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}