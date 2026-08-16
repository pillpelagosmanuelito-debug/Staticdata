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
import com.educalab.staticdata.ui.theme.*

/**
 * Insignias ilustradas de la colección de Staticdata. Cada código de badge
 * (definido en el seed) tiene una silueta propia, dibujada con Canvas.
 * Si el badge aún no está desbloqueado se dibuja en gris "tras cristal".
 */
@Composable
fun BadgeArt(code: String, unlocked: Boolean, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(72.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f * 0.92f
        val base = if (unlocked) badgeBaseColor(code) else Color(0xFFB9BFCC)
        val accent = if (unlocked) Color.White else Color(0xFFDADFE8)

        drawCircle(base, radius = r, center = center)
        drawCircle(base.copy(alpha = 0.55f), radius = r, center = center, style = Stroke(width = 5f))

        when (code) {
            "PRIMEROS_PASOS" -> drawMagnifier(center, r, accent)
            "INVESTIGADOR_CONSTANTE" -> drawFolderStack(center, r, accent)
            "MAESTRO_DE_CASOS" -> drawTrophy(center, r, accent)
            "CEREBRO_DE_DATOS" -> drawBulb(center, r, accent)
            "COLECCIONISTA_DE_DATOS" -> drawStackedStars(center, r, accent)
            "RACHA_PERFECTA" -> drawBolt(center, r, accent)
            "NIVEL_EXPERTO" -> drawFlag(center, r, accent)
            "DETECTIVE_COMPLETO" -> drawDiamond(center, r, accent)
            "CIENTIFICO_DE_MUESTRAS" -> drawFlask(center, r, accent)
            "ENCUESTADOR_ESTRELLA" -> drawClipboard(center, r, accent)
            else -> drawStar(center, r * 0.5f, accent)
        }
    }
}

private fun badgeBaseColor(code: String): Color = when (code) {
    "PRIMEROS_PASOS" -> TealClueDark
    "INVESTIGADOR_CONSTANTE" -> AmberStampDark
    "MAESTRO_DE_CASOS" -> BadgeGold
    "CEREBRO_DE_DATOS" -> VioletMystery
    "COLECCIONISTA_DE_DATOS" -> CoralAlert
    "RACHA_PERFECTA" -> AmberStamp
    "NIVEL_EXPERTO" -> TealClue
    "DETECTIVE_COMPLETO" -> InkNavy700
    "CIENTIFICO_DE_MUESTRAS" -> TealClueDark
    "ENCUESTADOR_ESTRELLA" -> VioletMystery
    else -> InkNavy600
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMagnifier(c: Offset, r: Float, color: Color) {
    val lensCenter = Offset(c.x - r * 0.12f, c.y - r * 0.12f)
    drawCircle(color, radius = r * 0.32f, center = lensCenter, style = Stroke(width = r * 0.14f))
    drawLine(color, lensCenter + Offset(r * 0.22f, r * 0.22f), c + Offset(r * 0.42f, r * 0.42f), strokeWidth = r * 0.14f, cap = StrokeCap.Round)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFolderStack(c: Offset, r: Float, color: Color) {
    listOf(-0.18f, 0f, 0.18f).forEachIndexed { i, dy ->
        drawRoundRect(color.copy(alpha = 0.4f + i * 0.3f), topLeft = Offset(c.x - r * 0.42f, c.y - r * 0.15f + r * dy), size = Size(r * 0.84f, r * 0.34f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.06f))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTrophy(c: Offset, r: Float, color: Color) {
    drawRoundRect(color, topLeft = Offset(c.x - r * 0.28f, c.y - r * 0.4f), size = Size(r * 0.56f, r * 0.5f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.1f))
    drawRect(color, topLeft = Offset(c.x - r * 0.16f, c.y + r * 0.1f), size = Size(r * 0.32f, r * 0.22f))
    drawLine(color, Offset(c.x - r * 0.4f, c.y - r * 0.3f), Offset(c.x - r * 0.55f, c.y - r * 0.05f), strokeWidth = r * 0.08f, cap = StrokeCap.Round)
    drawLine(color, Offset(c.x + r * 0.4f, c.y - r * 0.3f), Offset(c.x + r * 0.55f, c.y - r * 0.05f), strokeWidth = r * 0.08f, cap = StrokeCap.Round)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBulb(c: Offset, r: Float, color: Color) {
    drawCircle(color, radius = r * 0.32f, center = Offset(c.x, c.y - r * 0.1f))
    drawRect(color, topLeft = Offset(c.x - r * 0.14f, c.y + r * 0.18f), size = Size(r * 0.28f, r * 0.16f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStackedStars(c: Offset, r: Float, color: Color) {
    drawStar(Offset(c.x - r * 0.28f, c.y + r * 0.15f), r * 0.22f, color)
    drawStar(Offset(c.x + r * 0.28f, c.y + r * 0.15f), r * 0.22f, color)
    drawStar(Offset(c.x, c.y - r * 0.25f), r * 0.28f, color)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBolt(c: Offset, r: Float, color: Color) {
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(c.x + r * 0.12f, c.y - r * 0.42f)
        lineTo(c.x - r * 0.22f, c.y + r * 0.05f)
        lineTo(c.x + r * 0.02f, c.y + r * 0.05f)
        lineTo(c.x - r * 0.12f, c.y + r * 0.42f)
        lineTo(c.x + r * 0.3f, c.y - r * 0.08f)
        lineTo(c.x + r * 0.06f, c.y - r * 0.08f)
        close()
    }
    drawPath(path, color)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFlag(c: Offset, r: Float, color: Color) {
    drawLine(color, Offset(c.x - r * 0.3f, c.y - r * 0.42f), Offset(c.x - r * 0.3f, c.y + r * 0.42f), strokeWidth = r * 0.08f, cap = StrokeCap.Round)
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(c.x - r * 0.26f, c.y - r * 0.4f)
        lineTo(c.x + r * 0.35f, c.y - r * 0.22f)
        lineTo(c.x - r * 0.26f, c.y - r * 0.04f)
        close()
    }
    drawPath(path, color)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDiamond(c: Offset, r: Float, color: Color) {
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(c.x, c.y - r * 0.42f)
        lineTo(c.x + r * 0.36f, c.y)
        lineTo(c.x, c.y + r * 0.42f)
        lineTo(c.x - r * 0.36f, c.y)
        close()
    }
    drawPath(path, color)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFlask(c: Offset, r: Float, color: Color) {
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(c.x - r * 0.14f, c.y - r * 0.4f)
        lineTo(c.x + r * 0.14f, c.y - r * 0.4f)
        lineTo(c.x + r * 0.14f, c.y - r * 0.05f)
        lineTo(c.x + r * 0.36f, c.y + r * 0.4f)
        lineTo(c.x - r * 0.36f, c.y + r * 0.4f)
        lineTo(c.x - r * 0.14f, c.y - r * 0.05f)
        close()
    }
    drawPath(path, color)
    drawLine(color, Offset(c.x - r * 0.2f, c.y - r * 0.42f), Offset(c.x + r * 0.2f, c.y - r * 0.42f), strokeWidth = r * 0.08f, cap = StrokeCap.Round)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawClipboard(c: Offset, r: Float, color: Color) {
    drawRoundRect(color, topLeft = Offset(c.x - r * 0.3f, c.y - r * 0.4f), size = Size(r * 0.6f, r * 0.8f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.08f), style = Stroke(width = r * 0.09f))
    drawLine(color, Offset(c.x - r * 0.14f, c.y - r * 0.15f), Offset(c.x + r * 0.14f, c.y - r * 0.15f), strokeWidth = r * 0.07f, cap = StrokeCap.Round)
    drawLine(color, Offset(c.x - r * 0.14f, c.y + r * 0.1f), Offset(c.x + r * 0.14f, c.y + r * 0.1f), strokeWidth = r * 0.07f, cap = StrokeCap.Round)
}
