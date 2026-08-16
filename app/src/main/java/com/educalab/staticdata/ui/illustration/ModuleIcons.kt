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

enum class ModuleIcon {
    FOLDER_MAGNIFIER, DATA_QUESTION, TYPES_BLOCKS, SURVEY_CLIPBOARD,
    SORT_FUNNEL, TABLE_GRID, PIE_PERCENT, FLASK_SAMPLE, CASE_STAMP, TROPHY_PROGRESS, MAP_COMPASS
}

/** Iconografía diferenciada por módulo, coherente con la estética de la agencia. */
@Composable
fun ModuleIconArt(icon: ModuleIcon, tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(40.dp)) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f * 0.85f
        val stroke = Stroke(width = r * 0.16f, cap = StrokeCap.Round)
        when (icon) {
            ModuleIcon.FOLDER_MAGNIFIER -> {
                drawRoundRect(tint, topLeft = Offset(c.x - r * 0.7f, c.y - r * 0.3f), size = Size(r * 1.4f, r * 0.9f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.12f), style = stroke)
                drawCircle(tint, radius = r * 0.28f, center = Offset(c.x + r * 0.15f, c.y - r * 0.55f), style = Stroke(width = r * 0.12f))
                drawLine(tint, Offset(c.x + r * 0.36f, c.y - r * 0.34f), Offset(c.x + r * 0.5f, c.y - r * 0.2f), strokeWidth = r * 0.12f, cap = StrokeCap.Round)
            }
            ModuleIcon.DATA_QUESTION -> {
                drawCircle(tint, radius = r * 0.75f, center = c, style = stroke)
                drawArc(tint, startAngle = 200f, sweepAngle = 220f, useCenter = false, topLeft = Offset(c.x - r * 0.32f, c.y - r * 0.5f), size = Size(r * 0.64f, r * 0.6f), style = Stroke(width = r * 0.12f, cap = StrokeCap.Round))
                drawCircle(tint, radius = r * 0.07f, center = Offset(c.x, c.y + r * 0.32f))
            }
            ModuleIcon.TYPES_BLOCKS -> {
                drawRoundRect(tint, topLeft = Offset(c.x - r * 0.7f, c.y - r * 0.55f), size = Size(r * 0.55f, r * 0.55f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.08f), style = stroke)
                drawCircle(tint, radius = r * 0.3f, center = Offset(c.x + r * 0.45f, c.y + r * 0.15f), style = stroke)
            }
            ModuleIcon.SURVEY_CLIPBOARD -> {
                drawRoundRect(tint, topLeft = Offset(c.x - r * 0.5f, c.y - r * 0.7f), size = Size(r, r * 1.4f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.12f), style = stroke)
                drawLine(tint, Offset(c.x - r * 0.22f, c.y - r * 0.15f), Offset(c.x + r * 0.22f, c.y - r * 0.15f), strokeWidth = r * 0.1f, cap = StrokeCap.Round)
                drawLine(tint, Offset(c.x - r * 0.22f, c.y + r * 0.2f), Offset(c.x + r * 0.1f, c.y + r * 0.2f), strokeWidth = r * 0.1f, cap = StrokeCap.Round)
            }
            ModuleIcon.SORT_FUNNEL -> {
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(c.x - r * 0.7f, c.y - r * 0.6f); lineTo(c.x + r * 0.7f, c.y - r * 0.6f)
                    lineTo(c.x + r * 0.18f, c.y + r * 0.05f); lineTo(c.x + r * 0.18f, c.y + r * 0.65f)
                    lineTo(c.x - r * 0.18f, c.y + r * 0.65f); lineTo(c.x - r * 0.18f, c.y + r * 0.05f); close()
                }
                drawPath(path, tint, style = stroke)
            }
            ModuleIcon.TABLE_GRID -> {
                drawRoundRect(tint, topLeft = Offset(c.x - r * 0.7f, c.y - r * 0.55f), size = Size(r * 1.4f, r * 1.1f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.1f), style = stroke)
                drawLine(tint, Offset(c.x - r * 0.7f, c.y), Offset(c.x + r * 0.7f, c.y), strokeWidth = r * 0.08f)
                drawLine(tint, Offset(c.x, c.y - r * 0.55f), Offset(c.x, c.y + r * 0.55f), strokeWidth = r * 0.08f)
            }
            ModuleIcon.PIE_PERCENT -> {
                drawArc(tint, startAngle = -90f, sweepAngle = 230f, useCenter = true, topLeft = Offset(c.x - r * 0.7f, c.y - r * 0.7f), size = Size(r * 1.4f, r * 1.4f))
                drawArc(Color.White, startAngle = -90f + 230f, sweepAngle = 130f, useCenter = true, topLeft = Offset(c.x - r * 0.7f, c.y - r * 0.7f), size = Size(r * 1.4f, r * 1.4f), alpha = 0.001f)
            }
            ModuleIcon.FLASK_SAMPLE -> {
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(c.x - r * 0.2f, c.y - r * 0.6f); lineTo(c.x + r * 0.2f, c.y - r * 0.6f)
                    lineTo(c.x + r * 0.2f, c.y - r * 0.05f); lineTo(c.x + r * 0.55f, c.y + r * 0.6f)
                    lineTo(c.x - r * 0.55f, c.y + r * 0.6f); lineTo(c.x - r * 0.2f, c.y - r * 0.05f); close()
                }
                drawPath(path, tint, style = stroke)
            }
            ModuleIcon.CASE_STAMP -> {
                drawCircle(tint, radius = r * 0.72f, center = c, style = Stroke(width = r * 0.12f))
                drawCircle(tint, radius = r * 0.42f, center = c, style = Stroke(width = r * 0.08f))
            }
            ModuleIcon.TROPHY_PROGRESS -> {
                drawRoundRect(tint, topLeft = Offset(c.x - r * 0.35f, c.y - r * 0.5f), size = Size(r * 0.7f, r * 0.65f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.1f), style = stroke)
                drawLine(tint, Offset(c.x - r * 0.5f, c.y - r * 0.35f), Offset(c.x - r * 0.7f, c.y - r * 0.05f), strokeWidth = r * 0.1f, cap = StrokeCap.Round)
                drawLine(tint, Offset(c.x + r * 0.5f, c.y - r * 0.35f), Offset(c.x + r * 0.7f, c.y - r * 0.05f), strokeWidth = r * 0.1f, cap = StrokeCap.Round)
            }
            ModuleIcon.MAP_COMPASS -> {
                drawCircle(tint, radius = r * 0.72f, center = c, style = stroke)
                drawLine(tint, Offset(c.x, c.y - r * 0.4f), Offset(c.x + r * 0.18f, c.y), strokeWidth = r * 0.1f, cap = StrokeCap.Round)
                drawLine(tint, Offset(c.x, c.y + r * 0.4f), Offset(c.x - r * 0.18f, c.y), strokeWidth = r * 0.1f, cap = StrokeCap.Round)
            }
        }
    }
}
