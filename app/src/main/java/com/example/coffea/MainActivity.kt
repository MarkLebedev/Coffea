package com.example.coffea

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coffea.ui.theme.CoffeaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoffeaTheme {
                AppScreen()
            }
        }
    }
}

enum class Screen() {
    RECIPE_LIST,
    NEW_RECIPE,
    EDIT_RECIPE
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AppScreen(recipeViewModel: NewRecipeViewModel = viewModel(factory = AppViewModelProvider.Factory),
              recipeListViewModel: RecipeListViewModel = viewModel(factory = AppViewModelProvider.Factory),
              navController: NavHostController = rememberNavController(),
              modifier: Modifier = Modifier) {

    NavHost(navController = navController, startDestination = Screen.RECIPE_LIST.name, modifier = modifier) {
        composable(route = Screen.RECIPE_LIST.name) {
            RecipeList(
                recipeViewModel = recipeViewModel,
                recipeListViewModel = recipeListViewModel,
                onNewButtonCliked = {
                    navController.navigate(Screen.NEW_RECIPE.name)
                                    },
                onEditButtonClicked = {
                    navController.navigate(Screen.EDIT_RECIPE.name)
                })
        }

        composable(route = Screen.NEW_RECIPE.name) {
            RecipeEditor(
                newRecipe = true,
                recipeViewModel = recipeViewModel,
                onSaveClicked = {
                    navController.popBackStack(Screen.RECIPE_LIST.name, false)
                },
                onCancelClicked = {
                    navController.popBackStack(Screen.RECIPE_LIST.name, false)
                },
                onDeleteClicked = {}
            )
        }

        composable(route = Screen.EDIT_RECIPE.name) {
            RecipeEditor(
                newRecipe = false,
                recipeViewModel = recipeViewModel,
                onSaveClicked = {
                    navController.popBackStack(Screen.RECIPE_LIST.name, false)
                },
                onCancelClicked = {
                    navController.popBackStack(Screen.RECIPE_LIST.name, false)
                },
                onDeleteClicked = {

                    navController.popBackStack(Screen.RECIPE_LIST.name, false)
                }
            )
        }
    }
}

@Composable
@Preview
fun AppScreenPreview() {
    AppScreen()
}