package com.example.smartfood.ModelResponse

import java.util.Date

data class InventoryResponse (
    var id : Int,
    var currentSystem : Date,
    var totalInventory: Double
    )