package com.example.smartfood.Service

import com.example.smartfood.ModelResponse.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIServiceTrending {
    @GET
    suspend fun getAllProducts(@Url url:String): Response<List<ProductResponse>>
}