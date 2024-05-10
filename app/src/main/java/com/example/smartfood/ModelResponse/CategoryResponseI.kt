package com.example.smartfood.ModelResponse

data class CategoryResponseI (
    var categoryName : String,
    var products : List<ProductWithQuantityReponse>
)