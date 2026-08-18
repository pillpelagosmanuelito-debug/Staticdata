package com.educalab.staticdata.ui.screens.survey

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.ui.components.AgencyTopBar
import com.educalab.staticdata.ui.components.FrequencyBarChart
import com.educalab.staticdata.ui.components.ModuleHeroBanner
import com.educalab.staticdata.ui.components.SectionLabel
import com.educalab.staticdata.ui.illustration.ModuleIcon
import com.educalab.staticdata.ui.theme.PinkPop
import com.educalab.staticdata.util.LocalAppContainer
import com.educalab.staticdata.util.rememberAppViewModel

@Composable
fun SurveyScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val viewModel = rememberAppViewModel { SurveyViewModel(container.surveyRepository, container.frequencyRepository, container.createSurveyUseCase) }
    val state by viewModel.state.collectAsState()

    var question by remember { mutableStateOf("") }
    var optionInputs by remember { mutableStateOf(listOf("", "")) }
    var alias by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AgencyTopBar(title = "Encuestas locales", onBack = onBack)
        LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.weight(1f)) {
            item {
                ModuleHeroBanner(
                    icon = ModuleIcon.SURVEY_CLIPBOARD,
                    accent = PinkPop,
                    message = "Crea tu propia encuesta y descubre lo que opinan tus amigos."
                )
                Spacer(Modifier.height(16.dp))
                SectionLabel("Crear una nueva encuesta")
                OutlinedTextField(
                    value = question,
                    onValueChange = { if (it.length <= 80) question = it },
                    label = { Text("Pregunta (máx. 80 caracteres)") },
                    supportingText = { Text("${question.length}/80") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                optionInputs.forEachIndexed { index, value ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = { new -> optionInputs = optionInputs.toMutableList().also { it[index] = new } },
                        label = { Text("Opción ${index + 1}") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                    )
                }
                Row(modifier = Modifier.padding(top = 6.dp)) {
                    if (optionInputs.size < 6) {
                        TextButton(onClick = { optionInputs = optionInputs + "" }) { Text("+ Añadir opción") }
                    }
                    if (optionInputs.size > 2) {
                        TextButton(onClick = { optionInputs = optionInputs.dropLast(1) }) { Text("Quitar última") }
                    }
                }
                state.errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(6.dp))
                Button(
                    onClick = { viewModel.createSurvey(question, optionInputs); question = ""; optionInputs = listOf("", "") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Publicar encuesta") }

                Spacer(Modifier.height(20.dp))
                SectionLabel("Tus encuestas (${state.surveys.size})")
            }

            items(state.surveys) { survey ->
                val isSelected = survey.id == state.selectedSurveyId
                Card(
                    onClick = { viewModel.selectSurvey(survey.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(survey.question, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                if (isSelected) {
                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                        Text("Responder como:", style = MaterialTheme.typography.labelMedium)
                        OutlinedTextField(
                            value = alias, onValueChange = { alias = it }, singleLine = true,
                            label = { Text("Alias del que responde (opcional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        Row {
                            state.options.forEach { option ->
                                Box(
                                    modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(50))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .clickable { viewModel.respond(option.id, alias) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) { Text(option.label) }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        state.table?.let { FrequencyBarChart(table = it) }
                    }
                }
            }
        }
    }
}
