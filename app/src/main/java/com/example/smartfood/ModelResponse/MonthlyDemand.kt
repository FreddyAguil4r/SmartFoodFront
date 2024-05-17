package com.example.smartfood.ModelResponse

data class MonthlyDemand(
    val mes: String,
    val numero: String,
    val data: List<ProductDemand>
)