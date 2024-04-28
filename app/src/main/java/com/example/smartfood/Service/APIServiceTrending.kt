package com.example.smartfood.Service

import com.example.smartfood.ModelResponse.InventoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIServiceTrending {
    @GET
    suspend fun getInventory(@Url url:String): Response<InventoryResponse>
}