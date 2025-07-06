package com.example.saborchef.ui.publish

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saborchef.ui.components.RecipeForm

@Composable
fun EditRecipeScreen(
    recipeId: Long,
    navController: NavController
) {
    val context = LocalContext.current

    val viewModel: PublishRecipeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PublishRecipeViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return PublishRecipeViewModel(context.applicationContext as Application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(recipeId) {
        if (recipeId > 0L) {
            viewModel.loadRecipeById(recipeId)
        } else {
            Toast.makeText(context, "ID de receta inválido", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    when (uiState) {
        is PublishRecipeViewModel.PublishUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is PublishRecipeViewModel.PublishUiState.Error -> {
            val errorMessage = (uiState as PublishRecipeViewModel.PublishUiState.Error).message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: $errorMessage")
            }
        }

        else -> {
            RecipeForm(
                initialName = viewModel.nombreReceta,
                initialDescription = viewModel.descripcionReceta,
                initialDuration = viewModel.duracionReceta,
                initialPortions = viewModel.porcionesReceta,
                initialType = viewModel.tipoReceta,
                initialIngredients = viewModel.ingredientesList.toList(),
                initialSteps = viewModel.steps.toList(),
                viewModel = viewModel,
                navController = navController,
                onSubmit = { name, description, duration, portions, type, ingredients, steps, photos ->
                    viewModel.submitRecipe(
                        photos = photos,
                        nombre = name,
                        descripcion = description,
                        duracion = duration,
                        porciones = portions,
                        tipo = type,
                        ingredientes = ingredients,
                        pasos = steps
                    )
                }
            )
        }
    }

    if (uiState is PublishRecipeViewModel.PublishUiState.Success) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Receta guardada correctamente!", Toast.LENGTH_LONG).show()
            navController.navigate("my_recipes") {
                popUpTo("edit_recipe") { inclusive = true }
            }
            viewModel.resetState()
        }
    }
}

