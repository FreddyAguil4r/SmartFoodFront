package com.example.smartfood.ModelResponse

data class CategoryResponseI (
    var name : String,
    var totalValuesCategories : Double,
    var products : List<ProductResponseI>
)