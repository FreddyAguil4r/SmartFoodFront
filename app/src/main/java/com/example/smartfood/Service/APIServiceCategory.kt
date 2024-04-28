package com.example.smartfood.Service

import com.example.smartfood.ModelResponse.CategoryResponse
import com.example.smartfood.ModelResponse.CategoryResponseI
import com.example.smartfood.Request.CategoryRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface APIServiceCategory {
    @GET
    suspend fun getCategoryById(@Url url:String): Response<CategoryResponse>

    @GET
    suspend fun getAllCategories(@Url url:String): Response<List<CategoryResponse>>

    @GET
    suspend fun getAllCategoriesWithProducts(@Url url:String): Response<List<CategoryResponseI>>

    @POST("category")
    suspend fun addCategory(@Body supplier: CategoryRequest): Response<CategoryResponse>

    //@PUT("supplier/{id}")
    //suspend fun editCategory(@Path("id") id: Int, @Body supplier: SupplierRequest): Response<CategoryResponse>

    @DELETE("supplier/{id}")
    suspend fun deleteCategory(@Path("id") id: Int): Response<Unit>
}