package com.example.smartfood.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://smartfood-421500.uc.r.appspot.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}