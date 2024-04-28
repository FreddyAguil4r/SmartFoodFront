package com.example.smartfood.Model

import com.example.smartfood.ModelResponse.CategoryResponse
import com.example.smartfood.ModelResponse.SupplierResponse
import java.util.Date

data class Product(
    var id : Int,
    var name : String,
    var datePurchase : Date,
    var dueDate : Date,
    var unitCost: Double,
    var amount : Double,
    var warehouseValue : Double,
    var category : List<CategoryResponse>,
    var supplier : List<SupplierResponse>
)