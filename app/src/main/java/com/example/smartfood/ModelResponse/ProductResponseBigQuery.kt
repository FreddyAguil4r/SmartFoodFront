package com.example.smartfood.ModelResponse

data class ProductResponseBigQuery (
    var productId : Int,
    var productName : String,
    var cantidadComprar : Int,
    var mes : String,
    var anio : String,
)