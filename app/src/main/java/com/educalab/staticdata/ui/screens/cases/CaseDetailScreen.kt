package com.educalab.staticdata.ui.screens.cases

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.educalab.staticdata.domain.model.Exercise
import com.educalab.staticdata.domain.model.ExerciseType
import com.educalab.staticdata.ui.components.AgencyTopBar
import com.educalab.staticdata.ui.components.FeedbackPanel
import com.educalab.staticdata.ui.illustration.DatiMascot
import com.educalab.staticdata.ui.illustration.MascotMood
import com.educalab.staticdata.util.LocalAppContainer
import com.educalab.staticdata.util.rememberAppViewModel

@Composable
fun CaseDetailScreen(caseId: Long, onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val viewModel = rememberAppViewModel {
        CaseDetailViewModel(caseId, container.caseRepository, container.exerciseRepository, container.submitExerciseAnswerUseCase)
    }
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AgencyTopBar(title = state.case?.title ?: "Caso", onBack = onBack)

        if (state.loading || state.case == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { DatiMascot(mood = MascotMood.THINKING) }
            return
        }

        if (state.finished) {
            CaseFinishedView(onBack = onBack)
            return
        }

        val exercise = state.exercises.getOrNull(state.currentIndex)
        if (exercise == null) {
            CaseFinishedView(onBack = onBack)
            return
        }

        Column(modifier = Modifier.weight(1f).padding(16.dp)) {
            Text(
                "Reto ${state.currentIndex + 1} de ${state.exercises.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(state.case!!.briefing, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp))
            }
            Spacer(Modifier.height(14.dp))
            Text(exercise.prompt, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            val result = state.lastResult
            if (result == null) {
                ExerciseInteraction(exercise = exercise, onSubmit = { given -> viewModel.submitAnswer(given) })
            } else {
                FeedbackPanel(
                    correct = result.evaluation.correct,
                    explanation = result.evaluation.explanation,
                    onContinue = { viewModel.advance() }
                )
                if (result.newBadges.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text("🏅 ¡Nueva insignia desbloqueada: ${result.newBadges.joinToString { it.title }}!", style = MaterialTheme.typography.titleMedium)
                }
                if (result.leveledUp) {
                    Spacer(Modifier.height(6.dp))
                    Text("⭐ ¡Subiste de nivel!", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun CaseFinishedView(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DatiMascot(mood = MascotMood.CELEBRATING)
        Spacer(Modifier.height(12.dp))
        Text("¡Caso resuelto!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Dati ha archivado tus conclusiones en la Agencia.", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBack) { Text("Volver a la oficina de casos") }
    }
}

/**
 * Interacción adaptada al tipo de ejercicio: ordenar por toques (secuencia),
 * seleccionar una o varias opciones, o clasificar cada elemento con dos
 * botones. Nunca es "un solo botón A/B/C/D" para todos los tipos.
 */
@Composable
private fun ExerciseInteraction(exercise: Exercise, onSubmit: (List<String>) -> Unit) {
    when (exercise.type) {
        ExerciseType.ORDENAR_FRECUENCIA -> OrderInteraction(exercise, onSubmit)
        ExerciseType.CLASIFICAR_TIPO -> ClassifyInteraction(exercise, onSubmit)
        else -> ChoiceInteraction(exercise, onSubmit)
    }
}

@Composable
private fun OrderInteraction(exercise: Exercise, onSubmit: (List<String>) -> Unit) {
    var sequence by remember(exercise.id) { mutableStateOf(listOf<String>()) }
    val remaining = exercise.options.filter { it !in sequence }

    Column {
        Text("Toca las opciones en el orden correcto (de más a menos frecuente):", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth().height(48.dp)) {
            sequence.forEachIndexed { i, label ->
                Box(
                    modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer).padding(horizontal = 10.dp, vertical = 8.dp)
                ) { Text("${i + 1}. $label", style = MaterialTheme.typography.labelLarge) }
            }
        }
        Spacer(Modifier.height(12.dp))
        FlowChips(items = remaining, onClick = { label -> sequence = sequence + label })
        Spacer(Modifier.height(16.dp))
        Row {
            if (sequence.isNotEmpty()) {
                Button(onClick = { sequence = sequence.dropLast(1) }, colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors()) { Text("Deshacer") }
                Spacer(Modifier.width(8.dp))
            }
            Button(onClick = { onSubmit(sequence) }, enabled = sequence.size == exercise.options.size) { Text("Comprobar orden") }
        }
    }
}

@Composable
private fun ChoiceInteraction(exercise: Exercise, onSubmit: (List<String>) -> Unit) {
    val allowMultiple = exercise.correctAnswer.size > 1
    var selected by remember(exercise.id) { mutableStateOf(setOf<String>()) }

    Column {
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.height(160.dp)) {
            items(exercise.options) { option ->
                val isSelected = option in selected
                Card(
                    onClick = {
                        selected = if (allowMultiple) {
                            if (isSelected) selected - option else selected + option
                        } else {
                            setOf(option)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.padding(6.dp).fillMaxWidth()
                ) {
                    Text(
                        option,
                        modifier = Modifier.padding(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { onSubmit(selected.toList()) }, enabled = selected.isNotEmpty()) { Text("Comprobar respuesta") }
    }
}

@Composable
private fun ClassifyInteraction(exercise: Exercise, onSubmit: (List<String>) -> Unit) {
    var answers by remember(exercise.id) { mutableStateOf(List(exercise.options.size) { "" }) }

    Column {
        exercise.options.forEachIndexed { index, item ->
            Card(shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(item, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Row {
                        listOf("Categórica", "Numérica").forEach { option ->
                            val chosen = answers[index] == option
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (chosen) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { answers = answers.toMutableList().also { it[index] = option } }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(option, color = if (chosen) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { onSubmit(answers) }, enabled = answers.all { it.isNotEmpty() }) { Text("Comprobar clasificación") }
    }
}

@Composable
private fun FlowChips(items: List<String>, onClick: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(androidx.compose.foundation.rememberScrollState())) {
        items.forEach { label ->
            Box(
                modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onClick(label) }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) { Text(label, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}
