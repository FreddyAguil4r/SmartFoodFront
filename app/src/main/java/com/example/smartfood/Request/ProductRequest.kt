package com.example.smartfood.Request

import java.util.Date

data class ProductRequest(
    val name: String,
    val categoryId: Int,
    val unitId: Int
    )