package com.example.coffea.data

import kotlinx.coroutines.flow.Flow

class OfflineRecipeRepository(private val recipeDao: RecipeDao) : RecipeRepository {
    override fun getAll(): Flow<List<Recipe>> = recipeDao.getAll()

    override fun getByName(recipe_name: String): Recipe = recipeDao.getByName(recipe_name)

    override suspend fun insert(recipe: Recipe) = recipeDao.insert(recipe)

    override suspend fun delete(recipe: Recipe) = recipeDao.delete(recipe)
}