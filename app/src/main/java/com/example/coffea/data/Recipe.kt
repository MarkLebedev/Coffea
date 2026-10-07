package com.example.coffea.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey
    var name: String,
    var method: String,
    var coffee: Double,
    var grind: Int,
    var water: Double,
    var comment: String
)
