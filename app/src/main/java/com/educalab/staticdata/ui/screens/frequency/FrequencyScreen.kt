package com.educalab.staticdata.ui.screens.frequency

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.ui.components.AgencyTopBar
import com.educalab.staticdata.ui.components.FrequencyBarChart
import com.educalab.staticdata.ui.components.SectionLabel
import com.educalab.staticdata.util.LocalAppContainer
import com.educalab.staticdata.util.rememberAppViewModel

@Composable
fun FrequencyScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val viewModel = rememberAppViewModel { FrequencyViewModel(container.caseRepository, container.frequencyRepository) }
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AgencyTopBar(title = "Tablas de frecuencia", onBack = onBack)

        LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.weight(1f)) {
            item {
                SectionLabel("Elige un expediente de datos")
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    state.datasets.forEach { dataset ->
                        val active = dataset.id == state.activeDataset?.id
                        Card(
                            onClick = { viewModel.selectDataset(dataset.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                dataset.title, modifier = Modifier.padding(10.dp),
                                color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    "Hay ${state.values.size} datos en total. Escribe cuántas veces crees que aparece cada opción y comprueba tu tabla.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(10.dp))
            }

            items(state.labels) { label ->
                val correctness = viewModel.isRowCorrect(label)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                    OutlinedTextField(
                        value = state.userCounts[label] ?: "",
                        onValueChange = { viewModel.updateCount(label, it) },
                        modifier = Modifier.width(90.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = correctness == false
                    )
                    Spacer(Modifier.width(8.dp))
                    if (correctness != null) {
                        Text(if (correctness) "✅" else "❌", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
                Button(onClick = { viewModel.checkTable() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Comprobar tabla de frecuencias")
                }
                Spacer(Modifier.height(16.dp))
                state.revealed?.let { table ->
                    SectionLabel("Tabla real (calculada por StatsEngine)")
                    FrequencyBarChart(table = table)
                }
            }
        }
    }
}
