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

private data class AvatarSpec(val skin: Color, val gear: Color, val gearType: Int)

// 8 avatares base, cada uno con un color de piel/tono y un accesorio de
// "equipo de detective" distinto (lupa, gorra, gafas, pañuelo, auriculares,
// visera, binoculares, casco). Ninguno requiere foto real ni datos personales.
private val AVATARS = listOf(
    AvatarSpec(Color(0xFFE8B08C), Color(0xFF2FB6A3), 0),
    AvatarSpec(Color(0xFF8D5A3B), Color(0xFFE8A23B), 1),
    AvatarSpec(Color(0xFFF2D2A9), Color(0xFF8B6FE0), 2),
    AvatarSpec(Color(0xFFC98255), Color(0xFFE85D4E), 3),
    AvatarSpec(Color(0xFFFFE0BD), Color(0xFF1D8A7A), 4),
    AvatarSpec(Color(0xFF6E4630), Color(0xFFF2C14E), 5),
    AvatarSpec(Color(0xFFE0A177), Color(0xFF33406B), 6),
    AvatarSpec(Color(0xFFF7C99E), Color(0xFFC57F1F), 7),
)

@Composable
fun AvatarArt(avatarId: Int, modifier: Modifier = Modifier) {
    val spec = AVATARS[avatarId.coerceIn(0, AVATARS.size - 1)]
    Canvas(modifier = modifier.size(88.dp)) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f * 0.85f

        drawCircle(spec.gear.copy(alpha = 0.22f), radius = r * 1.05f, center = c)
        // cabeza
        drawCircle(spec.skin, radius = r * 0.62f, center = c)
        // ojos
        drawCircle(Color(0xFF1A2340), radius = r * 0.06f, center = Offset(c.x - r * 0.22f, c.y - r * 0.02f))
        drawCircle(Color(0xFF1A2340), radius = r * 0.06f, center = Offset(c.x + r * 0.22f, c.y - r * 0.02f))
        // sonrisa
        drawArc(Color(0xFF1A2340), startAngle = 15f, sweepAngle = 150f, useCenter = false,
            topLeft = Offset(c.x - r * 0.22f, c.y + r * 0.02f), size = androidx.compose.ui.geometry.Size(r * 0.44f, r * 0.3f),
            style = Stroke(width = r * 0.05f, cap = StrokeCap.Round))

        when (spec.gearType) {
            0 -> { // lupa
                drawCircle(spec.gear, radius = r * 0.22f, center = Offset(c.x + r * 0.5f, c.y - r * 0.5f), style = Stroke(width = r * 0.07f))
                drawLine(spec.gear, Offset(c.x + r * 0.66f, c.y - r * 0.34f), Offset(c.x + r * 0.82f, c.y - r * 0.18f), strokeWidth = r * 0.07f, cap = StrokeCap.Round)
            }
            1 -> drawArc(spec.gear, startAngle = 180f, sweepAngle = 180f, useCenter = true, topLeft = Offset(c.x - r * 0.65f, c.y - r * 0.85f), size = androidx.compose.ui.geometry.Size(r * 1.3f, r * 0.9f)) // gorra
            2 -> { // gafas
                drawCircle(spec.gear, radius = r * 0.18f, center = Offset(c.x - r * 0.22f, c.y - r * 0.02f), style = Stroke(width = r * 0.05f))
                drawCircle(spec.gear, radius = r * 0.18f, center = Offset(c.x + r * 0.22f, c.y - r * 0.02f), style = Stroke(width = r * 0.05f))
                drawLine(spec.gear, Offset(c.x - r * 0.05f, c.y - r * 0.02f), Offset(c.x + r * 0.05f, c.y - r * 0.02f), strokeWidth = r * 0.05f)
            }
            3 -> drawArc(spec.gear, startAngle = 200f, sweepAngle = 140f, useCenter = false, topLeft = Offset(c.x - r * 0.62f, c.y - r * 0.75f), size = androidx.compose.ui.geometry.Size(r * 1.24f, r * 0.7f), style = Stroke(width = r * 0.16f, cap = StrokeCap.Round)) // pañuelo/banda
            4 -> { // auriculares
                drawArc(spec.gear, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(c.x - r * 0.62f, c.y - r * 0.75f), size = androidx.compose.ui.geometry.Size(r * 1.24f, r * 1.1f), style = Stroke(width = r * 0.08f, cap = StrokeCap.Round))
                drawCircle(spec.gear, radius = r * 0.13f, center = Offset(c.x - r * 0.62f, c.y - r * 0.05f))
                drawCircle(spec.gear, radius = r * 0.13f, center = Offset(c.x + r * 0.62f, c.y - r * 0.05f))
            }
            5 -> drawRoundRect(spec.gear, topLeft = Offset(c.x - r * 0.65f, c.y - r * 0.85f), size = androidx.compose.ui.geometry.Size(r * 1.3f, r * 0.32f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.1f)) // visera plana
            6 -> { // binoculares
                drawCircle(spec.gear, radius = r * 0.16f, center = Offset(c.x - r * 0.2f, c.y - r * 0.05f), style = Stroke(width = r * 0.06f))
                drawCircle(spec.gear, radius = r * 0.16f, center = Offset(c.x + r * 0.2f, c.y - r * 0.05f), style = Stroke(width = r * 0.06f))
            }
            else -> { // casco explorador
                drawArc(spec.gear, startAngle = 180f, sweepAngle = 180f, useCenter = true, topLeft = Offset(c.x - r * 0.66f, c.y - r * 0.88f), size = androidx.compose.ui.geometry.Size(r * 1.32f, r * 0.95f))
                drawRoundRect(spec.gear, topLeft = Offset(c.x - r * 0.7f, c.y - r * 0.42f), size = androidx.compose.ui.geometry.Size(r * 1.4f, r * 0.12f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.06f))
            }
        }
    }
}

const val AVATAR_COUNT = 8
