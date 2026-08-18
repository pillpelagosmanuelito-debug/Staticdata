package com.educalab.staticdata.ui.screens.academy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.ui.components.AgencyTopBar
import com.educalab.staticdata.ui.components.FeedbackPanel
import com.educalab.staticdata.ui.components.ModuleHeroBanner
import com.educalab.staticdata.ui.illustration.DatiMascot
import com.educalab.staticdata.ui.illustration.MascotMood
import com.educalab.staticdata.ui.illustration.ModuleIcon
import com.educalab.staticdata.ui.theme.SkyBlue
import com.educalab.staticdata.util.rememberAppViewModel

@Composable
fun AcademyScreen(onBack: () -> Unit) {
    val viewModel = rememberAppViewModel { AcademyViewModel() }
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AgencyTopBar(title = "Academia de datos", onBack = onBack)
        ModuleHeroBanner(
            icon = ModuleIcon.DATA_QUESTION,
            accent = SkyBlue,
            message = "Dati te ayuda a distinguir datos categóricos de numéricos.",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(12.dp))

        Column(modifier = Modifier.padding(20.dp).weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Un dato es cualquier información que recogemos sobre algo: puede ser una categoría (cualidad) o un número (cantidad).",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))

            if (state.finished) {
                DatiMascot(mood = MascotMood.CELEBRATING)
                Spacer(Modifier.height(12.dp))
                Text("¡Ronda terminada!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Acertaste ${state.correctCount} de ${viewModel.items.size}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.restart() }) { Text("Repasar de nuevo") }
                return@Column
            }

            val item = viewModel.items[state.index]
            Spacer(Modifier.height(8.dp))
            Text("Ítem ${state.index + 1} de ${viewModel.items.size}", style = MaterialTheme.typography.labelLarge)

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text(
                    item.label,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(28.dp).fillMaxWidth()
                )
            }

            if (state.lastAnswerCorrect == null) {
                Text("¿Es un dato categórico o numérico?", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                Row {
                    Button(onClick = { viewModel.answer(true) }, modifier = Modifier.weight(1f).padding(end = 6.dp)) { Text("Categórica") }
                    Button(onClick = { viewModel.answer(false) }, modifier = Modifier.weight(1f).padding(start = 6.dp)) { Text("Numérica") }
                }
            } else {
                FeedbackPanel(
                    correct = state.lastAnswerCorrect!!,
                    explanation = item.hint,
                    onContinue = { viewModel.next() }
                )
            }
        }
    }
}
