package com.example.smartfood.ModelResponse

import java.util.Date

data class ProductResponse(
    var productId : Int,
    var productName : String,
    var quantity : Int,
    var totalInventory : Double
)