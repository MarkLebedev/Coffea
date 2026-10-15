package com.example.coffea.data

import java.util.UUID

data class NewRecipeUiState(
    var name: String = "",
    var method: String = "",
    var coffee: String = "",
    var grind: String = "",
    var water: String = "",
    var comment: String = ""
)