package com.example.saborchef.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.saborchef.ui.screens.Recipe
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins

@Composable
fun ResultsList(recipes: List<Recipe>, onRecipeClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(recipes) { recipe ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRecipeClick(recipe.id) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = recipe.title,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(end = 8.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            recipe.title,
                            fontSize = 16.sp,
                            fontFamily = Poppins,
                            color = BlueDark
                        )
                        Text(
                            "⏱ ${recipe.duration} | 🍽 ${recipe.portions} porciones",
                            fontSize = 12.sp,
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }
    }
}
