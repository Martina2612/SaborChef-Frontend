package com.example.saborchef.ui.components

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DishPosition(
    val imageOffsetX: Dp = 0.dp,
    val imageOffsetY: Dp = 0.dp,
    val textOffsetX: Dp = 0.dp,
    val textOffsetY: Dp = 0.dp,
    val startAngle: Float = 0f
)

data class Recipe(
    val id: Int,
    val title: String,
    val imageRes: Int
)

// Ejemplo de datos dummy (podés cambiar las imágenes por tus recursos)
val sampleRecipes = listOf(
    Recipe(1, "Ensalada César", com.example.saborchef.R.drawable.img_ratatouille),
    Recipe(2, "Tarta de Manzana", com.example.saborchef.R.drawable.img_cheesecake),
    Recipe(3, "Pasta Alfredo", com.example.saborchef.R.drawable.img_spaghetti)
)

