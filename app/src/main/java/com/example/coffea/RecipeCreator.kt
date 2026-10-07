package com.example.coffea

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.byValue
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.selectAll
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coffea.data.Recipe
import com.example.coffea.ui.theme.CoffeaTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffea.data.NewRecipeUiState
import kotlinx.coroutines.launch

fun calculateRatio(coffee: Double, water: Double): String {
    val ratio: String = if (water != 0.0) { "%.2f".format(coffee*1000/water) } else {"0.0"}
    return ratio
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun RecipeEditor(
    newRecipe: Boolean,
    recipeViewModel: NewRecipeViewModel = viewModel(),
    onSaveClicked: () -> Unit,
    onCancelClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier.safeContentPadding()) {

    val coroutineScope = rememberCoroutineScope()

    val recipeUiState by recipeViewModel.uiState.collectAsState()

    val ratio = calculateRatio(recipeViewModel.coffeeState.text.toString().toDoubleOrNull() ?: 0.0, recipeViewModel.waterState.text.toString().toDoubleOrNull() ?: 0.0)

    val initialRecipe by remember { mutableStateOf(Recipe(
        name = recipeUiState.name,
        method = recipeUiState.method,
        coffee = recipeUiState.coffee.toDoubleOrNull() ?: 0.0,
        grind = recipeUiState.grind.toIntOrNull() ?: 0,
        water = recipeUiState.water.toDoubleOrNull() ?: 0.0,
        comment = recipeUiState.comment)) }

    Surface() {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()))
        {
        if (newRecipe) {
            Text(
                text = "New Recipe!",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            Text(
                text = "Edit Recipe",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
        InputList(recipeViewModel, recipeUiState)
        Text(text = "Ratio: $ratio g/L", style = MaterialTheme.typography.bodyMedium)
        Button(onClick = {
            coroutineScope.launch {
                recipeViewModel.deleteRecipe(initialRecipe)
                recipeViewModel.addRecipe()
                recipeViewModel.resetRecipe()
                onSaveClicked()
            }
        }, modifier = Modifier
            .padding(4.dp)
            .width(200.dp)) {
            Text(text = "Save recipe", style = MaterialTheme.typography.labelMedium)
        }
        if (!newRecipe) {
            Button(onClick = {
                coroutineScope.launch {
                    recipeViewModel.deleteRecipe(initialRecipe)
                    recipeViewModel.resetRecipe()
                    onDeleteClicked()
                }
            }, modifier = Modifier
                .padding(4.dp)
                .width(200.dp)) {
                Text(text = "Delete", style = MaterialTheme.typography.labelMedium)
            }
        }
        OutlinedButton(onClick = {
            recipeViewModel.resetRecipe()
            onCancelClicked()
                                 }, modifier = Modifier
            .padding(4.dp)
            .width(200.dp)) {
            Text(text = "Cancel", style = MaterialTheme.typography.labelMedium)
        }
        }
    }

}

@Composable
fun InputField(label: String,
               recipeViewModel: NewRecipeViewModel,
               modifier: Modifier = Modifier) {

    Log.d("UISTATE", recipeViewModel.uiState.collectAsState().value.name)

    val fieldState = when (label) {
        "Recipe name" -> recipeViewModel.nameState
        "Brewing method" -> recipeViewModel.methodState
        "Coffee amount" -> recipeViewModel.coffeeState
        "Water amount" -> recipeViewModel.waterState
        "Grind setting" -> recipeViewModel.grindState
        "Comments" -> recipeViewModel.commentState
        else -> rememberTextFieldState()
    }

    val keyboardOption by remember { mutableStateOf(
        when (label) {
            "Recipe name", "Brewing method" -> KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
            "Coffee amount", "Grind setting", "Water amount" -> KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            "Comments" -> KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done)
            else -> KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
        }
    ) }

    val inputTransformation: InputTransformation? = when (label) {
        "Coffee amount", "Water amount" -> InputTransformation.byValue { _, proposed ->
            proposed.replace("[- ,]".toRegex(), "")}
        "Grind setting" -> InputTransformation.byValue { _, proposed ->
            proposed.replace("[- .,]".toRegex(), "")}
        else -> null
    }

    OutlinedTextField(
        label = { Text(text = label, style = MaterialTheme.typography.labelLarge) },
        lineLimits = TextFieldLineLimits.SingleLine,
        state = fieldState,
        keyboardOptions = keyboardOption,
        inputTransformation = inputTransformation,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .onFocusChanged {
                focusState ->
                if (focusState.isFocused) {
                    fieldState.edit { selectAll() }
                }
            }
    )
}

@Composable
fun InputList(
    recipeViewModel: NewRecipeViewModel,
    recipeUiState: NewRecipeUiState
) {

    Column(){
        InputField(label = "Recipe name", recipeViewModel = recipeViewModel)
        InputField(label = "Brewing method", recipeViewModel = recipeViewModel)
        InputField(label = "Coffee amount", recipeViewModel = recipeViewModel)
        InputField(label = "Grind setting", recipeViewModel = recipeViewModel)
        InputField(label = "Water amount", recipeViewModel = recipeViewModel)
        InputField(label = "Comments", recipeViewModel = recipeViewModel)
    }
}



//@Composable
//@Preview(showBackground = true)
//fun FieldPreview() {
//    CoffeaTheme(){
//        var value by remember { mutableStateOf("") }
//        InputField(value, onValueChange = {value = it})
//    }
//}

//@Composable
//@Preview(showBackground = true)
//fun RecipeCreatorPreview() {
//    CoffeaTheme(){
//        RecipeEditor(
//            newRecipe = true,
//            recipeViewModel = TODO(),
//            onSaveClicked = TODO(),
//            onCancelClicked = TODO(),
//            onDeleteClicked = {},
//            modifier = TODO()
//        )
//    }
//}

