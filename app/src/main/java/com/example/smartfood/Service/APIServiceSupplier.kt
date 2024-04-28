package com.example.smartfood.Service

import com.example.smartfood.ModelResponse.SupplierResponse
import com.example.smartfood.Request.SupplierRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url


interface APIServiceSupplier {
    @GET
    suspend fun getSupplierById(@Url url:String): Response<SupplierResponse>

    @GET
    suspend fun getAllSuplier(@Url url:String): Response<List<SupplierResponse>>

    @POST("supplier")
    suspend fun addSupplier(@Body supplier: SupplierRequest): Response<SupplierResponse>

    //@PUT("supplier/{id}")
    //suspend fun editSupplier(@Path("id") id: Int, @Body supplier: SupplierRequest): Response<SupplierResponse>

    @DELETE("supplier/{id}")
    suspend fun deleteSupplier(@Path("id") id: Int): Response<Unit>
}