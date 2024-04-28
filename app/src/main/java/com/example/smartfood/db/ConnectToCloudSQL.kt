package com.example.smartfood.db

import java.sql.DriverManager
/*
class ConnectToCloudSQL(val url: String,val user:String,val password:String) :Runnable {
    override fun run() {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            val connection = DriverManager.getConnection(url,user,password)
            connection.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}

interface APIServiceSupplier {
    @GET("supplier/{id}")
    suspend fun getSupplierById(@Path("id") id: Int): Response<SupplierResponse>

    @GET("supplier/all")
    suspend fun getAllSupplier(): Response<List<SupplierResponse>>

    @POST("supplier")
    suspend fun addSupplier(@Body supplier: SupplierRequest): Response<SupplierResponse>

    @PUT("supplier/{id}")
    suspend fun editSupplier(@Path("id") id: Int, @Body supplier: SupplierRequest): Response<SupplierResponse>

    @DELETE("supplier/{id}")
    suspend fun deleteSupplier(@Path("id") id: Int): Response<Unit>
}



*/
