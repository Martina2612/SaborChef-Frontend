package com.example.saborchef.model

/**
 * Representa un paso de la receta, con texto y una lista de imágenes/videos
 */
data class Step(
    val description: String,
    val media: List<Int>
)

/**
 * Modelo de receta completo para el DetailScreen
 */
data class Recipe(
    val id: String,
    val title: String,
    val user: String,            // nombre del autor
    val rating: Int,             // 0..5 estrellas
    val duration: String,        // ej. "15 min"
    val portions: String,        // ej. "2 porciones"
    val images: List<Int>,       // recursos drawable para carousel principal
    val ingredients: List<String>,
    val steps: List<Step>,       // lista de pasos con media
    val comments: List<Pair<String, String>>  // nombre + texto comentario
)
