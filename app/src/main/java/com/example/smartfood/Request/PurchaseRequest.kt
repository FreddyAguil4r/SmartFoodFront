package com.example.smartfood.Request

data class PurchaseRequest(
    val amount : Int,
    val unitCost : Double,
    val productId : Int,
    val supplierId : Int,
    val unitId : Int
)