package com.example.coffea

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.lifecycle.ViewModel
import com.example.coffea.data.NewRecipeUiState
import com.example.coffea.data.Recipe
import com.example.coffea.data.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.w3c.dom.Text
import kotlin.text.toIntOrNull

class NewRecipeViewModel(private val recipeRepository: RecipeRepository): ViewModel() {

    private val _uiState = MutableStateFlow(NewRecipeUiState())
    val uiState: StateFlow<NewRecipeUiState> = _uiState.asStateFlow()

    val nameState = TextFieldState()
    val methodState = TextFieldState()
    val coffeeState = TextFieldState()
    val grindState = TextFieldState()
    val waterState = TextFieldState()
    val commentState = TextFieldState()

    fun resetRecipe() {
        _uiState.update {
                currentState -> currentState.copy(
            name = "",
            method = "",
            coffee = "0.0",
            grind = "0",
            water = "0.0",
            comment = ""
        )
        }

        nameState.setTextAndPlaceCursorAtEnd(_uiState.value.name)
        methodState.setTextAndPlaceCursorAtEnd(_uiState.value.method)
        coffeeState.setTextAndPlaceCursorAtEnd(_uiState.value.coffee)
        grindState.setTextAndPlaceCursorAtEnd(_uiState.value.grind)
        waterState.setTextAndPlaceCursorAtEnd(_uiState.value.water)
        commentState.setTextAndPlaceCursorAtEnd(_uiState.value.comment)
    }

    fun stateToRecipe(): Recipe {
        return Recipe(
            name = _uiState.value.name,
            method = _uiState.value.method,
            coffee = _uiState.value.coffee.toDoubleOrNull() ?: 0.0,
            grind = _uiState.value.grind.toIntOrNull() ?: 0,
            water = _uiState.value.water.toDoubleOrNull() ?: 0.0,
            comment = _uiState.value.comment
        )
    }

    suspend fun addRecipe() {
        val recipe = Recipe(
            name = nameState.text.toString(),
            method = methodState.text.toString(),
            coffee = coffeeState.text.toString().toDoubleOrNull() ?: 0.0,
            grind = grindState.text.toString().toIntOrNull() ?: 0,
            water = waterState.text.toString().toDoubleOrNull() ?: 0.0,
            comment = commentState.text.toString(),
        )

        recipeRepository.insert(recipe)
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        recipeRepository.delete(recipe)
    }

    fun loadRecipeToState(recipe: Recipe) {
        _uiState.update { currentState -> currentState.copy(
            name = recipe.name,
            method = recipe.method,
            coffee = recipe.coffee.toString(),
            grind = recipe.grind.toString(),
            water = recipe.water.toString(),
            comment = recipe.comment
        ) }

        nameState.setTextAndPlaceCursorAtEnd(_uiState.value.name)
        methodState.setTextAndPlaceCursorAtEnd(_uiState.value.method)
        coffeeState.setTextAndPlaceCursorAtEnd(_uiState.value.coffee)
        grindState.setTextAndPlaceCursorAtEnd(_uiState.value.grind)
        waterState.setTextAndPlaceCursorAtEnd(_uiState.value.water)
        commentState.setTextAndPlaceCursorAtEnd(_uiState.value.comment)
    }

}