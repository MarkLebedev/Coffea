package com.example.coffea

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.material.icons.sharp.KeyboardArrowDown
import androidx.compose.material.icons.sharp.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositionContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffea.data.Recipe
import com.example.coffea.ui.theme.CoffeaTheme
import com.example.coffea.ui.theme.displayFontFamily
import kotlinx.coroutines.launch

fun shareRecipe(context: Context, recipe: Recipe) {
    val recipeText: String = "Recipe: ${recipe.name}\n" +
            "Method: ${recipe.method}\n" +
            "Coffee: ${recipe.coffee} g.\n" +
            "Water: ${recipe.water} g." +
            if (recipe.comment.length != 0) { "\nComment: ${recipe.comment}" } else {""}

    val sendIntent: Intent = Intent().apply {
        action = ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, recipeText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}


@Composable
fun RecipeCard(recipe: Recipe, onEditButtonClicked: (Recipe) -> Unit, isExpanded: Boolean = false, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(isExpanded) }
    val context = LocalContext.current
    Card(modifier
        .padding(8.dp)
        .animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )) {
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(end = 8.dp, start = 16.dp, top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth()

            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = recipe.name, style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(2f))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = recipe.method, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f))
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    IconButton(onClick = {
                        onEditButtonClicked(recipe)
                    }) {
                        Icon(Icons.Sharp.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = {expanded = !expanded }) {
                        if (expanded) {Icon(Icons.Sharp.KeyboardArrowUp, contentDescription = "Expand")} else {Icon(Icons.Sharp.KeyboardArrowDown, contentDescription = "Expand")}
                    }
                }
            }
            if (expanded) { Text(text = "Coffee: ${recipe.coffee}, Water: ${recipe.water}, Grind: ${recipe.grind}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 8.dp, start = 8.dp, top = 8.dp, bottom = 16.dp)) }
            if (expanded && recipe.comment.length!=0) {
                Text(text = recipe.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(end = 8.dp, start = 8.dp, top = 0.dp, bottom = 16.dp))
            }
            if (expanded) {
                Button(
                    onClick = {
                        shareRecipe(context, recipe)
                    },
                    modifier = Modifier.padding(end = 8.dp, start = 8.dp, top = 0.dp, bottom = 16.dp)
                ) {
                    Text(text = "Share")
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RecipeList(recipeViewModel: NewRecipeViewModel,
               recipeListViewModel: RecipeListViewModel,
               onNewButtonCliked: () -> Unit,
               onEditButtonClicked: () -> Unit,
               modifier: Modifier = Modifier) {
    val recipeListUiState by recipeListViewModel.uiState.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = { TopAppBar() },
        floatingActionButton = {FloatingActionButton(
            onClick = {
                recipeViewModel.resetRecipe()
                onNewButtonCliked()
                      },
        ) {Icon(Icons.Filled.Add, "Floating action button.")}}) { innerPadding ->
        Surface(modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            LazyColumn(Modifier) {
                items(recipeListUiState.recipeList) {item ->
                    RecipeCard(item, {
                        recipeViewModel.loadRecipeToState(item)
                        onEditButtonClicked() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(title = { Text(text = stringResource(R.string.app_name),
        style = MaterialTheme.typography.displayLarge,
        fontFamily = displayFontFamily,
        modifier = modifier.padding(8.dp)
    ) })
}

@Composable
@Preview(showBackground = true)
fun RecipeListPreview() {
    CoffeaTheme() {
        RecipeList(
            viewModel(),
            recipeListViewModel = viewModel(),
            {},
            {}
        )
    }
}

@Composable
@Preview
fun RecipeCardPreview() {
    CoffeaTheme() {
        RecipeCard(
            recipe = Recipe("Базовый фильтр", "V60", 24.0, 32, 400.0, "Это очень умный комментарий"),
            onEditButtonClicked = {},
            isExpanded = true,
            modifier = Modifier
        )
    }
}

//@Composable
//@Preview(showBackground = true, showSystemUi = true)
//fun AppPreview(modifier: Modifier = Modifier) {
//    CoffeaTheme {
//        AppScreen()
//    }
//}

