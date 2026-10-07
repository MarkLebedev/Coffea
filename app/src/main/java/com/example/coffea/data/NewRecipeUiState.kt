package com.example.coffea.data

data class NewRecipeUiState(
    var name: String = "",
    var method: String = "",
    var coffee: String = "0.0",
    var grind: String = "0",
    var water: String = "0.0",
    var comment: String = ""
)