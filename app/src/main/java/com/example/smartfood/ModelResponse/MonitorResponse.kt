package com.example.smartfood.ModelResponse

data class MonitorResponse(
    val totalInventario : Double,
    val totalCantidad : Int,
    var categories : List<CategoryTotal>,
)