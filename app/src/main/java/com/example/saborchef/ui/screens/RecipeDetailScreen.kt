package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.mock.sampleRecipes
import com.google.accompanist.pager.*
import com.example.saborchef.ui.theme.BlueDark

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onBack: () -> Unit = {}
) {
    val recipe = sampleRecipes.find { it.id == recipeId } ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {


        // 1) Carousel de imágenes principal
        val pagerState = rememberPagerState()
        Box {
            HorizontalPager(
                count = recipe.images.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) { page ->
                Image(
                    painter = painterResource(recipe.images[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            ) {
                repeat(recipe.images.size) { idx ->
                    val color = if (idx == pagerState.currentPage) BlueDark else Color.Gray
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .padding(4.dp)
                            .background(color, shape = CircleShape)
                    )
                }
            }
            // Flecha atrás
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .size(36.dp)
                    .background(Color.White, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Atrás",
                    tint = BlueDark
                )
            }
        }

        // Título centrado
        Text(
            text = recipe.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = BlueDark,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Spacer(Modifier.height(16.dp))

        // Fila: usuario (izq) y estrellas (der)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = recipe.user,
                fontSize = 16.sp,
                color = BlueDark
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(5) { idx ->
                    val icon = if (idx < recipe.rating) {
                        Icons.Filled.Star
                    } else {
                        Icons.Outlined.StarBorder
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = if (idx < recipe.rating) "Estrella llena" else "Estrella vacía",
                        tint = BlueDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

        }

        Spacer(Modifier.height(8.dp))

        // Fila: duración (izq) y porciones (der)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "⏱ ${recipe.duration}",
                fontSize = 16.sp,
                color = BlueDark
            )
            Text(
                text = "👥 ${recipe.portions}",
                fontSize = 16.sp,
                color = BlueDark
            )
        }

        Spacer(Modifier.height(16.dp))

        // Ingredientes
        Text(
            "Ingredientes",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = BlueDark,
            modifier = Modifier.padding(start = 16.dp)
        )
        recipe.ingredients.forEach { ing ->
            Text(
                "• $ing",
                fontSize = 16.sp,
                color = BlueDark,
                modifier = Modifier.padding(start = 24.dp, top = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Preparación
        Text(
            "Preparación",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = BlueDark,
            modifier = Modifier.padding(start = 16.dp)
        )
        recipe.steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, top = 12.dp, end = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Número en círculo
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .background(BlueDark, shape = CircleShape)
                ) {
                    Text(
                        text = "${index + 1}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = step.description,
                        fontSize = 16.sp,
                        color = BlueDark
                    )
                    // Carousel de media
                    val stepPager = rememberPagerState()
                    HorizontalPager(
                        count = step.media.size,
                        state = stepPager,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .padding(top = 8.dp)
                    ) { page ->
                        Image(
                            painter = painterResource(step.media[page]),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                        )
                    }
                    // Indicators paso
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        repeat(step.media.size) { idx2 ->
                            val dotColor = if (idx2 == stepPager.currentPage) BlueDark else Color.LightGray
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(2.dp)
                                    .background(dotColor, shape = CircleShape)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Comentarios
        Text(
            "Comentarios",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = BlueDark,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )
        recipe.comments.forEach { (user, comment) ->
            Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp)) {
                Text(user, fontWeight = FontWeight.Medium, color = BlueDark)
                Text(comment, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecipeDetailScreenPreview() {
    RecipeDetailScreen(recipeId = "1", onBack = {})
}
