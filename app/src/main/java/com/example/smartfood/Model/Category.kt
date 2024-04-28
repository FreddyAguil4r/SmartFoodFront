package com.example.smartfood.Model

data class Category(
    val Id: Long,
    val name : String,
    val total_value: Double,
    val mList: List<Product>
)