package com.example.smartfood.ModelResponse

data class PurchaseResponse(
    var id: Int,
    val amount : Int,
    val unitCost : Double,
    val productId : ProductResponse,
    val supplierId : SupplierResponse,
    val unitId : UnitResponse,
)