package com.example.smartfood.Service

import com.example.smartfood.ModelResponse.PurchaseResponse
import com.example.smartfood.Request.PurchaseRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIServicePurchase {
    @POST("purchase")
    suspend fun makePurchaseProduct(@Body purchase: PurchaseRequest): Response<PurchaseResponse>
}