package com.example.smartfood.Service

import com.example.smartfood.ModelResponse.UnitResponse
import com.example.smartfood.Request.SupplierRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

interface APIServiceUnit {
    @GET
    suspend fun getUnitById(@Url url:String): Response<UnitResponse>

    @GET
    suspend fun getAllUnits(@Url url:String): Response<List<UnitResponse>>
}