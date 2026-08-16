package com.educalab.staticdata.ui.illustration

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * Fondo decorativo de "constelación de datos": puntos conectados, evocando
 * un tablero de investigación con pistas relacionadas. Se usa como textura
 * suave detrás de la Oficina de Casos, nunca compite con el contenido.
 */
@Composable
fun DataConstellationBackground(color: Color, modifier: Modifier = Modifier, seed: Long = 7L, dotCount: Int = 26) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val rng = Random(seed)
        val points = (0 until dotCount).map {
            Offset(rng.nextFloat() * size.width, rng.nextFloat() * size.height)
        }
        points.forEachIndexed { i, p ->
            points.drop(i + 1).forEach { q ->
                val dist = kotlin.math.hypot((p.x - q.x).toDouble(), (p.y - q.y).toDouble())
                if (dist < size.width * 0.16) {
                    drawLine(color.copy(alpha = 0.14f), p, q, strokeWidth = 1.5f)
                }
            }
        }
        points.forEach { p -> drawCircle(color.copy(alpha = 0.35f), radius = 3.5f, center = p) }
    }
}
