package com.example.smartfood.Service

import com.example.smartfood.ModelResponse.LoginResponse
import com.example.smartfood.Request.LoginRequest
import com.example.smartfood.Request.UserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIServiceUser {
    @POST("user")
    suspend fun registerUser(@Body userRequest: UserRequest ):Response<Unit>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}