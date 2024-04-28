package com.example.smartfood.ModelResponse

import java.util.Date

data class ProductResponse(
    var id : Int,
    var name : String,
    var datePurchase : Date,
    var dueDate : Date,
    var unitCost: Double,
    var amount : Double,
    var warehouseValue : Double,
    var category : CategoryResponse,
    var supplier : SupplierResponse
)