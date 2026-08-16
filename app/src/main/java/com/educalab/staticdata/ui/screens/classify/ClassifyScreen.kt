package com.educalab.staticdata.ui.screens.classify

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.domain.model.DataValue
import com.educalab.staticdata.ui.components.AgencyTopBar
import com.educalab.staticdata.ui.illustration.DatiMascot
import com.educalab.staticdata.ui.illustration.MascotMood
import com.educalab.staticdata.util.LocalAppContainer
import com.educalab.staticdata.util.rememberAppViewModel

@Composable
fun ClassifyScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val viewModel = rememberAppViewModel { ClassifyViewModel(container.caseRepository) }
    val state by viewModel.state.collectAsState()
    var selected by remember(state.activeDataset?.id) { mutableStateOf<DataValue?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AgencyTopBar(title = "Organizador y clasificación", onBack = onBack)

        if (state.activeDataset == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { DatiMascot(mood = MascotMood.THINKING) }
            return
        }

        Column(modifier = Modifier.padding(16.dp).weight(1f)) {
            Text(state.activeDataset!!.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Toca un dato y luego el contenedor correcto para clasificarlo.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))

            Text("Contenedores", style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 6.dp)) {
                state.bins.forEach { bin ->
                    Card(
                        onClick = { selected?.let { viewModel.placeInBin(it, bin); selected = null } },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(bin, fontWeight = FontWeight.Bold)
                            Text("${state.placedCorrectly[bin] ?: 0} colocados", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Datos pendientes (${state.pending.size})", style = MaterialTheme.typography.labelLarge)
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.weight(1f)) {
                items(state.pending, key = { it.id }) { value ->
                    val isSelected = selected?.id == value.id
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selected = if (isSelected) null else value }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            value.label,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (state.roundComplete) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "¡Ronda completa! Aciertos: ${state.placedCorrectly.values.sum()} · Errores: ${state.mistakes}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { viewModel.startNewRound() }) { Text("Nueva ronda") }
            }
        }
    }
}
