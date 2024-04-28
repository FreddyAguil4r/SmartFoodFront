package com.example.smartfood.Request

import java.util.Date

data class ProductRequest(
    val name: String,
    val datePurchase: Date,
    val unitCost: Double,
    val amount: Double,
    val categoryId: Int,
    val supplierId: Int
    )