package com.example.saborchef.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.models.IngredienteCantidadDTO
import com.example.saborchef.models.RecetaEscaladaResponse
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.viewmodel.SaveState
import kotlin.math.roundToInt

@Composable
fun ScaledIngredientsDisplay(
    scaledRecipe: RecetaEscaladaResponse,
    saveState: SaveState,
    canSaveMore: Boolean,
    savedCount: Int,
    onSaveRecipe: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con info de escalado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Receta ajustada",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlueDark
                    )
                    Text(
                        text = "${scaledRecipe.porcionesOriginal} → ${scaledRecipe.porcionesEscaladas} porciones",
                        fontSize = 14.sp,
                        color = BlueDark.copy(alpha = 0.7f)
                    )
                }

                // Factor de escalado
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeDark.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = "×${String.format("%.2f", scaledRecipe.factorEscalado)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ingredientes escalados
            Text(
                text = "Ingredientes ajustados:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = BlueDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            scaledRecipe.ingredientes?.forEach { ingredient ->
                ScaledIngredientItem(ingredient = ingredient)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón guardar y contador
            Column {
                // Contador de recetas guardadas
                Text(
                    text = "Recetas guardadas: $savedCount/10",
                    fontSize = 12.sp,
                    color = BlueDark.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botón guardar
                Button(
                    onClick = onSaveRecipe,
                    enabled = canSaveMore && saveState !is SaveState.Loading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeDark,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    when (saveState) {
                        is SaveState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardando...", color = Color.White)
                        }
                        is SaveState.Success -> {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("¡Guardada!", color = Color.White)
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (canSaveMore) "Guardar receta" else "Límite alcanzado (10/10)",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScaledIngredientItem(ingredient: IngredienteCantidadDTO) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Punto indicador
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(OrangeDark, shape = RoundedCornerShape(3.dp))
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Texto del ingrediente
        val cantidadText = ingredient.cantidad?.let { cantidad ->
            // Redondear a 2 decimales pero mostrar entero si es .00
            val rounded = (cantidad * 100).roundToInt() / 100.0
            if (rounded == rounded.toInt().toDouble()) {
                rounded.toInt().toString()
            } else {
                String.format("%.2f", rounded)
            }
        } ?: ""

        val texto = listOfNotNull(
            cantidadText.takeIf { it.isNotEmpty() },
            ingredient.unidadDescripcion?.takeIf { it.isNotEmpty() },
            ingredient.nombreIngrediente
        ).joinToString(" ")

        Text(
            text = texto,
            fontSize = 14.sp,
            color = BlueDark,
            modifier = Modifier.weight(1f)
        )
    }
}