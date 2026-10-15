package com.example.coffea.data

import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getAll(): Flow<List<Recipe>>

    fun getNames(): Flow<List<String>>

    fun getByName(recipe_name: String): Recipe

    suspend fun insert(recipe: Recipe)

    suspend fun delete(recipe: Recipe)
}