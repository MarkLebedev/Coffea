package com.example.coffea

import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffea.data.Recipe
import com.example.coffea.data.RecipeListUiState
import com.example.coffea.data.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class RecipeListViewModel(private val recipeRepository: RecipeRepository): ViewModel() {
    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = recipeRepository.getAll().map { RecipeListUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RecipeListUiState()
        )

    suspend fun update_state_from_db() {
        _uiState.update { currentState -> currentState.copy(recipeRepository.getAll() as List<Recipe>) }
    }
}