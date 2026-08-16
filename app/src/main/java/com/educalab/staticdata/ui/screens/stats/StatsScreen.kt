package com.educalab.staticdata.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.ui.components.AgencyTopBar
import com.educalab.staticdata.ui.components.FrequencyBarChart
import com.educalab.staticdata.ui.components.SectionLabel
import com.educalab.staticdata.util.LocalAppContainer
import com.educalab.staticdata.util.rememberAppViewModel
import kotlin.math.abs

@Composable
fun StatsScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val viewModel = rememberAppViewModel { StatsViewModel(container.caseRepository) }
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState())) {
        AgencyTopBar(title = "Moda y porcentajes", onBack = onBack)

        Column(modifier = Modifier.padding(16.dp)) {
            SectionLabel("Elige un expediente")
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                state.datasets.forEach { dataset ->
                    val active = dataset.id == state.activeDataset?.id
                    Card(
                        onClick = { viewModel.selectDataset(dataset.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(dataset.title, modifier = Modifier.padding(10.dp), color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            state.table?.let { table ->
                SectionLabel("Tabla completa")
                FrequencyBarChart(table = table)
                Spacer(Modifier.height(8.dp))
                Text(
                    if (table.modes.size == 1) "La moda es: ${table.modes.first()}" else "Hay varias modas: ${table.modes.joinToString()}",
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(20.dp))
                SectionLabel("Calculadora de porcentajes: ¡predice antes de mirar!")
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    table.rows.forEach { row ->
                        val selected = row.label == state.selectedLabel
                        Card(
                            onClick = { viewModel.selectLabel(row.label) },
                            shape = RoundedCornerShape(50),
                            colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.padding(end = 8.dp)
                        ) { Text(row.label, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), color = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("Tu predicción: ${state.predictedPercentage.toInt()}%", style = MaterialTheme.typography.bodyLarge)
                Slider(
                    value = state.predictedPercentage,
                    onValueChange = { viewModel.updatePrediction(it) },
                    valueRange = 0f..100f
                )
                Button(onClick = { viewModel.reveal() }) { Text("Revelar porcentaje real") }

                state.revealedPercentage?.let { real ->
                    Spacer(Modifier.height(10.dp))
                    val diff = abs(real - state.predictedPercentage)
                    val message = when {
                        diff <= 5 -> "¡Excelente estimación! Estabas muy cerca."
                        diff <= 15 -> "Buena aproximación, ¡sigue practicando!"
                        else -> "El dato real está más lejos de lo que pensabas: fíjate en el conteo real."
                    }
                    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Porcentaje real de '${state.selectedLabel}': $real%", fontWeight = FontWeight.Bold)
                            Text(message, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
