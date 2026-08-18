package com.educalab.staticdata.ui.screens.frequency

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.ui.components.AgencyTopBar
import com.educalab.staticdata.ui.components.FrequencyBarChart
import com.educalab.staticdata.ui.components.ModuleHeroBanner
import com.educalab.staticdata.ui.components.RawDataEvidence
import com.educalab.staticdata.ui.components.SectionLabel
import com.educalab.staticdata.ui.components.TabChipsRow
import com.educalab.staticdata.ui.illustration.ModuleIcon
import com.educalab.staticdata.ui.theme.AmberStamp
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
                ModuleHeroBanner(
                    icon = ModuleIcon.TABLE_GRID,
                    accent = AmberStamp,
                    message = "Cuenta los datos y arma tu propia tabla de frecuencias."
                )
                Spacer(Modifier.height(16.dp))
                SectionLabel("Elige un expediente de datos")
                TabChipsRow(
                    items = state.datasets.map { it.id to it.title },
                    selectedId = state.activeDataset?.id,
                    onSelect = { viewModel.selectDataset(it) }
                )
                Spacer(Modifier.height(16.dp))
                RawDataEvidence(title = "Evidencia: los ${state.values.size} datos originales", values = state.values.map { it.label })
                Spacer(Modifier.height(14.dp))
                Text(
                    "Cuenta cuántas veces aparece cada opción en la evidencia de arriba y escribe el número:",
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
                    SectionLabel("Tabla real de frecuencias")
                    FrequencyBarChart(table = table)
                }
            }
        }
    }
}
