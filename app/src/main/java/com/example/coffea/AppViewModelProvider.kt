package com.example.coffea

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            NewRecipeViewModel(coffeaApplication().container.recipeRepository)
        }

        initializer {
            RecipeListViewModel(coffeaApplication().container.recipeRepository)
        }
    }
}

fun CreationExtras.coffeaApplication(): CoffeaApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as CoffeaApplication)