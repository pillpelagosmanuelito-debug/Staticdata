package com.educalab.staticdata.ui.screens.sampling

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.ui.components.AgencyTopBar
import com.educalab.staticdata.ui.components.SectionLabel
import com.educalab.staticdata.util.LocalAppContainer
import com.educalab.staticdata.util.rememberAppViewModel

@Composable
fun SamplingScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val viewModel = rememberAppViewModel { SamplingViewModel(container.sampleLabRepository, container.runSampleExperimentUseCase) }
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AgencyTopBar(title = "Laboratorio de muestras", onBack = onBack)

        LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.weight(1f)) {
            item {
                SectionLabel("Elige un experimento")
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    state.experiments.forEach { experiment ->
                        val active = experiment.id == state.activeExperiment?.id
                        Card(
                            onClick = { viewModel.selectExperiment(experiment.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(experiment.title, modifier = Modifier.padding(10.dp), color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                state.activeExperiment?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it.description, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(16.dp))
                Text("Tamaño de la muestra: ${state.sampleSize}", style = MaterialTheme.typography.bodyLarge)
                Slider(
                    value = state.sampleSize.toFloat(),
                    onValueChange = { viewModel.setSampleSize(it.toInt()) },
                    valueRange = 4f..20f, steps = 15
                )
                Button(onClick = { viewModel.drawSample() }, modifier = Modifier.fillMaxWidth()) {
                    Text("🔬 Extraer una muestra")
                }
                Spacer(Modifier.height(16.dp))
                SectionLabel("Tiradas realizadas (${state.runs.size})")
            }

            items(state.runs) { run ->
                Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Muestra de tamaño ${run.sampleSize}", fontWeight = FontWeight.Bold)
                        Text(run.drawnLabels.groupingBy { it }.eachCount().entries.joinToString { "${it.key}: ${it.value}" }, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            state.variability?.let { variability ->
                item {
                    Spacer(Modifier.height(12.dp))
                    SectionLabel("¿Cuánto varían las muestras entre sí?")
                    Text(
                        "Cada tirada da resultados parecidos pero no idénticos. Este es el rango de variación observado por opción:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(6.dp))
                    variability.rangeByLabel.entries.sortedByDescending { it.value }.forEach { (label, range) ->
                        Text("• $label: varía hasta ${(range * 100).toInt()} puntos porcentuales entre tiradas", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
