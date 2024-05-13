package com.example.smartfood.Service

import com.example.smartfood.ModelResponse.ProductResponse
import com.example.smartfood.Request.ProductRequest
import com.example.smartfood.Request.SubstractProductRequest
import com.example.smartfood.Request.SupplierRequest
import com.example.smartfood.Request.UpdateProductRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

interface APIServiceProduct {
    @GET
    suspend fun getProductById(@Url url:String): Response<ProductResponse>

    @GET
    suspend fun getAllProducts(@Url url:String): Response<List<ProductResponse>>


    @POST("product")
    suspend fun addProduct(@Body supplier: ProductRequest): Response<ProductResponse>

    @PUT("product/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: UpdateProductRequest): Response<ProductResponse>

    @PUT("product/substract")
    suspend fun substractProduct(@Body product: SubstractProductRequest): Response<Unit>

    @DELETE("product/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>
}