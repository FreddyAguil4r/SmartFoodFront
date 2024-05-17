package com.example.smartfood.Service

import com.example.smartfood.ModelResponse.ProductResponseBigQuery
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIServiceBigQuery {
    @GET
    suspend fun callSp(@Url url:String) :Response<Unit>
    @GET
    suspend fun getAllProductsWithDemand(@Url url:String): Response<List<ProductResponseBigQuery>>
}