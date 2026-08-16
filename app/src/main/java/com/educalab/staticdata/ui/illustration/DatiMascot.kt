package com.educalab.staticdata.ui.illustration

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.ui.theme.AmberStamp
import com.educalab.staticdata.ui.theme.InkNavy900
import com.educalab.staticdata.ui.theme.TealClue
import com.educalab.staticdata.ui.theme.TealClueDark

enum class MascotMood { NEUTRAL, HAPPY, THINKING, CELEBRATING }

/**
 * Dati: dron-detective de datos, mascota guía de la Agencia. Ilustración
 * 100% vectorial dibujada con Compose Canvas — sin dependencias externas,
 * funciona offline y se adapta a cualquier tamaño.
 */
@Composable
fun DatiMascot(mood: MascotMood = MascotMood.NEUTRAL, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(120.dp)) {
        val w = size.width
        val h = size.height
        val bodyRadius = w * 0.34f
        val center = Offset(w / 2f, h * 0.52f)

        // Rotor izquierdo/derecho (líneas + óvalos)
        drawLine(TealClueDark, Offset(center.x - bodyRadius * 1.6f, center.y - bodyRadius * 0.2f), Offset(center.x - bodyRadius * 0.9f, center.y - bodyRadius * 0.55f), strokeWidth = 5f, cap = StrokeCap.Round)
        drawLine(TealClueDark, Offset(center.x + bodyRadius * 1.6f, center.y - bodyRadius * 0.2f), Offset(center.x + bodyRadius * 0.9f, center.y - bodyRadius * 0.55f), strokeWidth = 5f, cap = StrokeCap.Round)
        drawOval(TealClue, topLeft = Offset(center.x - bodyRadius * 1.95f, center.y - bodyRadius * 0.42f), size = androidx.compose.ui.geometry.Size(bodyRadius * 0.9f, bodyRadius * 0.32f))
        drawOval(TealClue, topLeft = Offset(center.x + bodyRadius * 1.05f, center.y - bodyRadius * 0.42f), size = androidx.compose.ui.geometry.Size(bodyRadius * 0.9f, bodyRadius * 0.32f))

        // Antena
        drawLine(InkNavy900, Offset(center.x, center.y - bodyRadius), Offset(center.x, center.y - bodyRadius * 1.55f), strokeWidth = 5f, cap = StrokeCap.Round)
        drawCircle(AmberStamp, radius = bodyRadius * 0.12f, center = Offset(center.x, center.y - bodyRadius * 1.55f))

        // Cuerpo principal (cápsula)
        drawCircle(InkNavy900, radius = bodyRadius, center = center)
        drawCircle(TealClueDark, radius = bodyRadius * 0.94f, center = center, style = Stroke(width = 3f))

        // Visor / lente central (ojo tipo lupa)
        val eyeRadius = bodyRadius * 0.52f
        drawCircle(Color.White, radius = eyeRadius, center = center)
        val pupilOffset = when (mood) {
            MascotMood.THINKING -> Offset(center.x + eyeRadius * 0.25f, center.y - eyeRadius * 0.2f)
            else -> center
        }
        val pupilRadius = if (mood == MascotMood.CELEBRATING) eyeRadius * 0.4f else eyeRadius * 0.5f
        drawCircle(TealClueDark, radius = pupilRadius, center = pupilOffset)
        drawCircle(AmberStamp, radius = pupilRadius * 0.35f, center = pupilOffset)

        // Boca / expresión simple bajo el visor
        val mouthY = center.y + bodyRadius * 0.62f
        when (mood) {
            MascotMood.HAPPY, MascotMood.CELEBRATING -> {
                drawArc(
                    color = AmberStamp,
                    startAngle = 20f, sweepAngle = 140f, useCenter = false,
                    topLeft = Offset(center.x - bodyRadius * 0.35f, mouthY - bodyRadius * 0.28f),
                    size = androidx.compose.ui.geometry.Size(bodyRadius * 0.7f, bodyRadius * 0.4f),
                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                )
            }
            MascotMood.THINKING -> drawLine(AmberStamp, Offset(center.x - bodyRadius * 0.2f, mouthY), Offset(center.x + bodyRadius * 0.25f, mouthY), strokeWidth = 6f, cap = StrokeCap.Round)
            MascotMood.NEUTRAL -> drawLine(AmberStamp, Offset(center.x - bodyRadius * 0.22f, mouthY), Offset(center.x + bodyRadius * 0.22f, mouthY), strokeWidth = 6f, cap = StrokeCap.Round)
        }

        // Estrellitas de celebración
        if (mood == MascotMood.CELEBRATING) {
            listOf(-1.5f to -1.3f, 1.6f to -1.1f, -1.7f to 0.6f).forEach { (dx, dy) ->
                drawStar(Offset(center.x + bodyRadius * dx, center.y + bodyRadius * dy), bodyRadius * 0.14f, AmberStamp)
            }
        }
    }
}

internal fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(center: Offset, radius: Float, color: Color) {
    val path = androidx.compose.ui.graphics.Path()
    val points = 5
    for (i in 0 until points * 2) {
        val angle = Math.PI * i / points - Math.PI / 2
        val r = if (i % 2 == 0) radius else radius * 0.42f
        val x = center.x + (r * Math.cos(angle)).toFloat()
        val y = center.y + (r * Math.sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}
