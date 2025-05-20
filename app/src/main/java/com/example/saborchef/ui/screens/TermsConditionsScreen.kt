package com.example.saborchef.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.components.CurvedHeader
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.ui.theme.SaborChefTheme

@Composable
fun TermsConditionsScreen(
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Cabecera curvada
            CurvedHeader(
                title = "Términos y Condiciones",
                icon = Icons.Default.OutlinedFlag,
                headerColor = OrangeDark,
                circleColor = androidx.compose.ui.graphics.Color.White,
                onBack = onBack
            )

            // Contenido desplazable
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(end = 8.dp)) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = longTermsText,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Justify,
                        color = BlueDark,
                        lineHeight = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Scrollbar personalizada
                ScrollBar(
                    scrollState = scrollState
                )
            }
        }
    }
}


private val longTermsText = """
Bienvenido a SaborChef. Por favor lee detenidamente los siguientes Términos y Condiciones:

1. **Uso de la aplicación**  
   Esta aplicación está destinada exclusivamente para la gestión y registro de usuarios interesados en cursos de cocina. El usuario acepta utilizarla de manera responsable.

2. **Privacidad**  
   Los datos personales y de pago se almacenan de forma segura según nuestra política de privacidad. No compartiremos tu información con terceros sin tu consentimiento.

3. **Responsabilidades**  
   El usuario es responsable de la veracidad de los datos ingresados, así como del correcto cobro de los cursos a través de los medios de pago registrados.

4. **Cancelaciones y reembolsos**  
   Consulta nuestra política de cancelación; normalmente no se realizan reembolsos una vez iniciado un curso, salvo circunstancias excepcionales.

5. **Propiedad Intelectual**  
   Todos los contenidos, recetas y materiales de los cursos son propiedad de SaborChef y sus instructores, quedando prohibida su reproducción no autorizada.

6. **Modificaciones**  
   SaborChef se reserva el derecho de modificar estos Términos y Condiciones en cualquier momento; las actualizaciones se notificarán dentro de la app.

7. **Contactos**  
   Para cualquier consulta, escríbenos a soporte@saborchef.com.

…  
(continúa con todos los puntos que necesites)
""".trimIndent()


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ScrollBar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    width: Dp = 4.dp,
    color: Color = BlueDark,
    padding: Dp = 4.dp
) {
    val indicatorHeightFraction = 0.15f // Altura proporcional al contenido visible

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
    ) {
        val containerHeight = maxHeight
        val scrollProgress = scrollState.value.toFloat() / scrollState.maxValue.toFloat().coerceAtLeast(1f)
        val indicatorHeight = containerHeight * indicatorHeightFraction
        val indicatorOffset = (containerHeight - indicatorHeight) * scrollProgress

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = indicatorOffset + padding, end = padding)
                .width(width)
                .height(indicatorHeight)
                .background(color = color, shape = RoundedCornerShape(4.dp))
        )
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun TermsConditionsScreenPreview() {
    SaborChefTheme {
        Surface {
            TermsConditionsScreen(
                onBack = { /* Volver */ }
            )
        }
    }
}