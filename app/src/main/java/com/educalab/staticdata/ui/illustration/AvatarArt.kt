package com.educalab.staticdata.ui.illustration

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.ui.theme.AmberStamp
import com.educalab.staticdata.ui.theme.CoralAlert
import com.educalab.staticdata.ui.theme.InkNavy900
import com.educalab.staticdata.ui.theme.LimeZest
import com.educalab.staticdata.ui.theme.PinkPop
import com.educalab.staticdata.ui.theme.SkyBlue
import com.educalab.staticdata.ui.theme.SunYellow
import com.educalab.staticdata.ui.theme.TealClue
import com.educalab.staticdata.ui.theme.VioletMystery

private data class AvatarSpec(val skin: Color, val hair: Color, val badge: Color, val gear: Color, val gearType: Int)

// 8 avatares, cada uno con: color de piel, color de pelo, una "insignia" de
// fondo muy saturada (como un tablero de juego) y un accesorio de detective
// distinto (lupa, gorra, gafas, pañuelo, auriculares, visera, binoculares,
// casco). Ninguno requiere foto real ni datos personales.
private val AVATARS = listOf(
    AvatarSpec(Color(0xFFE8B08C), Color(0xFF3B2A1F), CoralAlert, TealClue, 0),
    AvatarSpec(Color(0xFF8D5A3B), Color(0xFF1A1A1A), SkyBlue, AmberStamp, 1),
    AvatarSpec(Color(0xFFF2D2A9), Color(0xFF6B3F1D), PinkPop, VioletMystery, 2),
    AvatarSpec(Color(0xFFC98255), Color(0xFFB33F1D), LimeZest, PinkPop, 3),
    AvatarSpec(Color(0xFFFFE0BD), Color(0xFFD4A017), AmberStamp, TealClue, 4),
    AvatarSpec(Color(0xFF6E4630), Color(0xFF241A12), VioletMystery, SunYellow, 5),
    AvatarSpec(Color(0xFFE0A177), Color(0xFF9B6BFF), TealClue, CoralAlert, 6),
    AvatarSpec(Color(0xFFF7C99E), Color(0xFF4A2C17), SunYellow, VioletMystery, 7),
)

@Composable
fun AvatarArt(avatarId: Int, modifier: Modifier = Modifier) {
    val spec = AVATARS[avatarId.coerceIn(0, AVATARS.size - 1)]
    Canvas(modifier = modifier.size(88.dp)) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f * 0.85f

        // Insignia de fondo bien saturada, como una placa de juego.
        drawCircle(spec.badge, radius = r * 1.05f, center = c)
        drawCircle(Color.White, radius = r * 1.05f, center = c, style = Stroke(width = r * 0.05f), alpha = 0.9f)

        // Pelo (asoma alrededor de la cabeza, como un halo desplazado hacia arriba).
        drawCircle(spec.hair, radius = r * 0.74f, center = Offset(c.x, c.y - r * 0.08f))
        // cabeza
        drawCircle(spec.skin, radius = r * 0.6f, center = c)
        // mejillas sonrosadas
        drawCircle(Color(0xFFFF8FA3).copy(alpha = 0.45f), radius = r * 0.1f, center = Offset(c.x - r * 0.34f, c.y + r * 0.16f))
        drawCircle(Color(0xFFFF8FA3).copy(alpha = 0.45f), radius = r * 0.1f, center = Offset(c.x + r * 0.34f, c.y + r * 0.16f))
        // cejas
        drawLine(InkNavy900, Offset(c.x - r * 0.32f, c.y - r * 0.16f), Offset(c.x - r * 0.14f, c.y - r * 0.19f), strokeWidth = r * 0.045f, cap = StrokeCap.Round)
        drawLine(InkNavy900, Offset(c.x + r * 0.14f, c.y - r * 0.19f), Offset(c.x + r * 0.32f, c.y - r * 0.16f), strokeWidth = r * 0.045f, cap = StrokeCap.Round)
        // ojos con brillo
        val leftEye = Offset(c.x - r * 0.22f, c.y - r * 0.02f)
        val rightEye = Offset(c.x + r * 0.22f, c.y - r * 0.02f)
        drawCircle(InkNavy900, radius = r * 0.065f, center = leftEye)
        drawCircle(InkNavy900, radius = r * 0.065f, center = rightEye)
        drawCircle(Color.White, radius = r * 0.02f, center = Offset(leftEye.x - r * 0.02f, leftEye.y - r * 0.02f))
        drawCircle(Color.White, radius = r * 0.02f, center = Offset(rightEye.x - r * 0.02f, rightEye.y - r * 0.02f))
        // sonrisa
        drawArc(InkNavy900, startAngle = 15f, sweepAngle = 150f, useCenter = false,
            topLeft = Offset(c.x - r * 0.22f, c.y + r * 0.02f), size = Size(r * 0.44f, r * 0.3f),
            style = Stroke(width = r * 0.05f, cap = StrokeCap.Round))

        when (spec.gearType) {
            0 -> { // lupa
                drawCircle(spec.gear, radius = r * 0.22f, center = Offset(c.x + r * 0.5f, c.y - r * 0.5f), style = Stroke(width = r * 0.07f))
                drawLine(spec.gear, Offset(c.x + r * 0.66f, c.y - r * 0.34f), Offset(c.x + r * 0.82f, c.y - r * 0.18f), strokeWidth = r * 0.07f, cap = StrokeCap.Round)
            }
            1 -> drawArc(spec.gear, startAngle = 180f, sweepAngle = 180f, useCenter = true, topLeft = Offset(c.x - r * 0.65f, c.y - r * 0.85f), size = Size(r * 1.3f, r * 0.9f)) // gorra
            2 -> { // gafas
                drawCircle(spec.gear, radius = r * 0.18f, center = Offset(c.x - r * 0.22f, c.y - r * 0.02f), style = Stroke(width = r * 0.05f))
                drawCircle(spec.gear, radius = r * 0.18f, center = Offset(c.x + r * 0.22f, c.y - r * 0.02f), style = Stroke(width = r * 0.05f))
                drawLine(spec.gear, Offset(c.x - r * 0.05f, c.y - r * 0.02f), Offset(c.x + r * 0.05f, c.y - r * 0.02f), strokeWidth = r * 0.05f)
            }
            3 -> drawArc(spec.gear, startAngle = 200f, sweepAngle = 140f, useCenter = false, topLeft = Offset(c.x - r * 0.62f, c.y - r * 0.75f), size = Size(r * 1.24f, r * 0.7f), style = Stroke(width = r * 0.16f, cap = StrokeCap.Round)) // pañuelo/banda
            4 -> { // auriculares
                drawArc(spec.gear, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(c.x - r * 0.62f, c.y - r * 0.75f), size = Size(r * 1.24f, r * 1.1f), style = Stroke(width = r * 0.08f, cap = StrokeCap.Round))
                drawCircle(spec.gear, radius = r * 0.13f, center = Offset(c.x - r * 0.62f, c.y - r * 0.05f))
                drawCircle(spec.gear, radius = r * 0.13f, center = Offset(c.x + r * 0.62f, c.y - r * 0.05f))
            }
            5 -> drawRoundRect(spec.gear, topLeft = Offset(c.x - r * 0.65f, c.y - r * 0.85f), size = Size(r * 1.3f, r * 0.32f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.1f)) // visera plana
            6 -> { // binoculares
                drawCircle(spec.gear, radius = r * 0.16f, center = Offset(c.x - r * 0.2f, c.y - r * 0.05f), style = Stroke(width = r * 0.06f))
                drawCircle(spec.gear, radius = r * 0.16f, center = Offset(c.x + r * 0.2f, c.y - r * 0.05f), style = Stroke(width = r * 0.06f))
            }
            else -> { // casco explorador
                drawArc(spec.gear, startAngle = 180f, sweepAngle = 180f, useCenter = true, topLeft = Offset(c.x - r * 0.66f, c.y - r * 0.88f), size = Size(r * 1.32f, r * 0.95f))
                drawRoundRect(spec.gear, topLeft = Offset(c.x - r * 0.7f, c.y - r * 0.42f), size = Size(r * 1.4f, r * 0.12f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.06f))
            }
        }
    }
}

const val AVATAR_COUNT = 8
