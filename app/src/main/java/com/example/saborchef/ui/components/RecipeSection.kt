// src/main/java/com/example/saborchef/ui/components/RecipeCarousel.kt
package com.example.saborchef.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
            color=BlueDark,
            modifier = Modifier.padding(start = 16.dp,top=15.dp, bottom = 8.dp)
        )

        if (items.isEmpty()) {
            Text(
                text = "No hay recetas",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
            return
        }

        val itemsPerPage = 2
        val pageCount = (items.size + itemsPerPage - 1) / itemsPerPage
        val pagerState = rememberPagerState(pageCount = { pageCount })

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            val startIndex = page * itemsPerPage
            val endIndex = minOf(startIndex + itemsPerPage, items.size)
            val pageItems = items.subList(startIndex, endIndex)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                        is TopRecetaResponse     -> item.fotoPrincipal ?: ""
                        is RecetaResumenResponse -> item.fotoPrincipal ?: ""
                        is RecetaDetalleResponse -> item.fotoPrincipal ?: ""
                        else                     -> ""
                    }
                    val nombre = when (item) {
                        is TopRecetaResponse     -> item.nombreReceta ?: ""
                        is RecetaResumenResponse -> item.nombre ?: ""
                        is RecetaDetalleResponse -> item.nombre ?: ""
                        else                     -> ""
                    }
                    val usuario = when (item) {

                        is RecetaResumenResponse -> item.nombreUsuario ?: ""
                        is RecetaDetalleResponse -> item.nombreUsuario ?: ""
                        else                     -> ""
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

        // Indicadores como circulitos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { idx2 ->
                val dotColor = if (idx2 == pagerState.currentPage) BlueDark else Color.LightGray

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(dotColor)
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick(id) }
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,       // recorta si hace falta
                modifier = Modifier
                    .fillMaxWidth()                    // ocupa TODO el ancho de la card
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = BlueDark,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = BlueLight,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

