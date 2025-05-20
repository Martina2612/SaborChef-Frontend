package com.example.saborchef.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import kotlin.math.*

@Composable
fun CurvedTextAroundCircle(
    text: String,
    modifier: Modifier = Modifier.size(120.dp),  // igual que la imagen
    radius: Float,                               // ej. 60f
    centerX: Float,                              // ej. 60f
    centerY: Float,                              // ej. 60f
    startAngle: Float = -90f,
    fontFamily: FontFamily,
    fontSize: TextUnit = 16.sp,
    color: Color = Color.White
) {
    Canvas(modifier = modifier) {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                isAntiAlias = true
                textSize = fontSize.toPx()
                this.color = color.toArgb()
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }

            // ancho total del texto
            val totalWidth = paint.measureText(text)
            // circunferencia del círculo de texto
            val circumference = 2 * PI * radius
            // ángulo a repartir por cada carácter
            val anglePerChar = (totalWidth / circumference * 360f) / text.length

            // empezamos desplazados la mitad del texto
            var angle = startAngle - (anglePerChar * text.length / 2)

            text.forEach { ch ->
                val rad = Math.toRadians(angle.toDouble())
                val x = centerX + radius * cos(rad).toFloat()
                val y = centerY + radius * sin(rad).toFloat()

                canvas.nativeCanvas.save()
                canvas.nativeCanvas.rotate((angle + 90).toFloat(), x, y)
                canvas.nativeCanvas.drawText(ch.toString(), x, y, paint)
                canvas.nativeCanvas.restore()

                angle += anglePerChar
            }
        }
    }
}

