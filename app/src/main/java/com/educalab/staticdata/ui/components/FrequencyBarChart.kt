package com.educalab.staticdata.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educalab.staticdata.domain.model.FrequencyTable

/**
 * Gráfico de barras real, dibujado a partir de una FrequencyTable calculada
 * por StatsEngine (nunca datos aleatorios ni de relleno). Cada barra muestra
 * su conteo y porcentaje.
 */
@Composable
fun FrequencyBarChart(table: FrequencyTable, barColor: Color = MaterialTheme.colorScheme.primary, modifier: Modifier = Modifier) {
    if (table.rows.isEmpty()) {
        Text("Todavía no hay datos para graficar.", style = MaterialTheme.typography.bodyMedium)
        return
    }
    val maxCount = table.rows.maxOf { it.count }.coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        table.rows.forEach { row ->
            val targetFraction = row.count.toFloat() / maxCount.toFloat()
            val animated by animateFloatAsState(targetValue = targetFraction, animationSpec = tween(500), label = "bar")
            Column(modifier = Modifier.padding(vertical = 5.dp)) {
                Text(
                    "${row.label}  ·  ${row.count} (${row.percentage}%)" + if (row.label in table.modes) "  ⭐ moda" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (row.label in table.modes) FontWeight.Bold else FontWeight.Normal
                )
                Canvas(modifier = Modifier.fillMaxWidth().height(18.dp).padding(top = 3.dp)) {
                    drawRoundRect(
                        color = barColor.copy(alpha = 0.15f),
                        size = androidx.compose.ui.geometry.Size(size.width, size.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
                    )
                    drawRoundRect(
                        color = if (row.label in table.modes) barColor else barColor.copy(alpha = 0.75f),
                        size = androidx.compose.ui.geometry.Size(size.width * animated, size.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
                    )
                }
            }
        }
        Text("Total: ${table.total} datos", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 6.dp))
    }
}
