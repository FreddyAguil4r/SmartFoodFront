package com.example.smartfood.Request


data class UpdateProductRequest(
    val name: String,
    val unitCost: Double,
    val amount: Double,
)