package com.example.smartfood.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Tiempo de conexión
        .readTimeout(30, TimeUnit.SECONDS)    // Tiempo de lectura
        .writeTimeout(30, TimeUnit.SECONDS)   // Tiempo de escritura
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://smartfood-421500.uc.r.appspot.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}