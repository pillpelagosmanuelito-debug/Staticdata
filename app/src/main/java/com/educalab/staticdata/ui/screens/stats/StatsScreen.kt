package com.educalab.staticdata.ui.screens.stats

import androidx.compose.foundation.background
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
import com.educalab.staticdata.ui.components.LabelChipsRow
import com.educalab.staticdata.ui.components.SectionLabel
import com.educalab.staticdata.ui.components.TabChipsRow
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
            TabChipsRow(
                items = state.datasets.map { it.id to it.title },
                selectedId = state.activeDataset?.id,
                onSelect = { viewModel.selectDataset(it) }
            )

            Spacer(Modifier.height(14.dp))
            state.table?.let { table ->
                SectionLabel("Tabla completa")
                FrequencyBarChart(table = table)
                Spacer(Modifier.height(8.dp))
                Text(
                    "La moda es el dato que más se repite.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    if (table.modes.size == 1) "La moda es: ${table.modes.first()}" else "Hay varias modas: ${table.modes.joinToString()}",
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(20.dp))
                SectionLabel("¿Qué parte del total es? ¡Adivina antes de mirar!")
                LabelChipsRow(
                    labels = table.rows.map { it.label },
                    selectedLabel = state.selectedLabel,
                    onSelect = { viewModel.selectLabel(it) }
                )

                Spacer(Modifier.height(12.dp))
                Text("Tu predicción: ${state.predictedPercentage.toInt()}%", style = MaterialTheme.typography.bodyLarge)
                Slider(
                    value = state.predictedPercentage,
                    onValueChange = { viewModel.updatePrediction(it) },
                    valueRange = 0f..100f
                )
                Button(onClick = { viewModel.reveal() }) { Text("Ver el porcentaje real") }

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
