// src/main/java/com/example/saborchef/ui/components/RecipeCarousel.kt
package com.example.saborchef.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.models.RecetaDetalleResponse
import com.example.saborchef.models.RecetaResumenResponse
import com.example.saborchef.models.TopRecetaResponse
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.BlueLight

@Composable
fun <T> RecipeCarouselSection(
    title: String,
    items: List<T>,
    onClick: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = BlueDark,
            modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 8.dp, end=8.dp)
        )

        if (items.isEmpty()) {
            Text(
                text = "No hay recetas",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
            return
        }

        val itemsPerPage = 3
        val pageCount = (items.size + itemsPerPage - 1) / itemsPerPage
        val pagerState = rememberPagerState(pageCount = { pageCount })

        Spacer(Modifier.height(20.dp))
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) { page ->
            val startIndex = page * itemsPerPage
            val endIndex = (startIndex + itemsPerPage).coerceAtMost(items.size)
            val pageItems = items.subList(startIndex, endIndex)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pageItems.forEach { item ->
                    // Extraemos datos
                    val id = when (item) {
                        is TopRecetaResponse     -> item.idReceta ?: 0L
                        is RecetaResumenResponse -> item.idReceta ?: 0L
                        is RecetaDetalleResponse -> item.idReceta ?: 0L
                        else                     -> 0L
                    }
                    val img = when (item) {
                        is TopRecetaResponse     -> item.fotoPrincipal.orEmpty()
                        is RecetaResumenResponse -> item.fotoPrincipal.orEmpty()
                        is RecetaDetalleResponse -> item.fotoPrincipal.orEmpty()
                        else                     -> ""
                    }
                    val nombre = when (item) {
                        is TopRecetaResponse     -> item.nombreReceta.orEmpty()
                        is RecetaResumenResponse -> item.nombre.orEmpty()
                        is RecetaDetalleResponse -> item.nombre.orEmpty()
                        else                     -> ""
                    }
                    val usuario = when (item) {
                        is TopRecetaResponse -> ""  // no mostramos usuario en Top
                        is RecetaResumenResponse -> item.nombreUsuario.orEmpty()
                        is RecetaDetalleResponse -> item.nombreUsuario.orEmpty()
                        else -> ""
                    }

                    RecipeCarouselCard(
                        id = id,
                        imageUrl = img,
                        title = nombre,
                        subtitle = usuario,
                        onClick = onClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(itemsPerPage - pageItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(15.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { idx ->
                val color = if (idx == pagerState.currentPage) BlueDark else Color.LightGray.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .size(if (idx == pagerState.currentPage) 10.dp else 8.dp)
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun RecipeCarouselCard(
    id: Long,
    imageUrl: String,
    title: String,
    subtitle: String,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick(id) }
    ) {
        // 1) Imagen sin padding, ocupa ancho total
        Base64Image(
            base64String = imageUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        )

        // 2) Resto del contenido con pequeño padding interior
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = BlueDark,
                modifier = Modifier.fillMaxWidth()
            )
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = BlueLight,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

