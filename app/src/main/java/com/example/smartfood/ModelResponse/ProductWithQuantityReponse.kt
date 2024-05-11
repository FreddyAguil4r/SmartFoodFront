package com.example.smartfood.ModelResponse

data class ProductWithQuantityReponse(
    var productId : Int,
    var productName : String,
    var quantity : Int,
    var totalInventory : Double
)