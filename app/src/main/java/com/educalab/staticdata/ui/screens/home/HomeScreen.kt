package com.educalab.staticdata.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.ui.components.XpProgressBar
import com.educalab.staticdata.ui.illustration.AvatarArt
import com.educalab.staticdata.ui.illustration.DataConstellationBackground
import com.educalab.staticdata.ui.illustration.DatiMascot
import com.educalab.staticdata.ui.illustration.MascotMood
import com.educalab.staticdata.ui.illustration.ModuleIcon
import com.educalab.staticdata.ui.illustration.ModuleIconArt
import com.educalab.staticdata.ui.navigation.Routes
import com.educalab.staticdata.util.LocalAppContainer
import com.educalab.staticdata.util.rememberAppViewModel

private data class HomeNode(val title: String, val teaser: String, val icon: ModuleIcon, val route: String)

private val NODES = listOf(
    HomeNode("Oficina de casos", "Investiga misiones reales con datos", ModuleIcon.FOLDER_MAGNIFIER, Routes.CASES),
    HomeNode("Academia", "Qué es un dato y tipos de datos", ModuleIcon.DATA_QUESTION, Routes.ACADEMY),
    HomeNode("Encuestas", "Crea tus propias encuestas", ModuleIcon.SURVEY_CLIPBOARD, Routes.SURVEY),
    HomeNode("Organizador", "Clasifica y ordena pistas", ModuleIcon.SORT_FUNNEL, Routes.CLASSIFY),
    HomeNode("Tablas de frecuencia", "Construye tablas con tus datos", ModuleIcon.TABLE_GRID, Routes.FREQUENCY),
    HomeNode("Moda y porcentajes", "Calcula e interpreta resultados", ModuleIcon.PIE_PERCENT, Routes.STATS),
    HomeNode("Laboratorio de muestras", "Compara muestras de una población", ModuleIcon.FLASK_SAMPLE, Routes.SAMPLING),
    HomeNode("Progreso y colección", "Insignias y estadísticas propias", ModuleIcon.TROPHY_PROGRESS, Routes.PROGRESS),
)

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val container = LocalAppContainer.current
    val viewModel = rememberAppViewModel { HomeViewModel(container.profileRepository, container.caseRepository) }
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        DataConstellationBackground(color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxSize())

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Agencia de Detectives de Datos", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "Hola, ${state.profile?.alias ?: "detective"}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onNavigate(Routes.PROFILE) }
                        .padding(2.dp)
                ) {
                    AvatarArt(avatarId = state.profile?.avatarId ?: 0, modifier = Modifier.size(52.dp))
                }
            }

            Spacer(Modifier.height(14.dp))
            XpProgressBar(currentXp = state.progress.totalXp, xpToNext = viewModel.xpToNextLevel(), level = state.progress.level)

            Spacer(Modifier.height(16.dp))
            NextMissionCard(state = state, onOpen = { onNavigate(Routes.CASES) })

            Spacer(Modifier.height(18.dp))
            Text("Tablero de la Agencia", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(NODES) { node ->
                    HomeNodeCard(node = node, onClick = { onNavigate(node.route) })
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun NextMissionCard(state: HomeUiState, onOpen: () -> Unit) {
    Card(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            DatiMascot(mood = MascotMood.HAPPY, modifier = Modifier.size(70.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Próxima misión", style = MaterialTheme.typography.labelMedium)
                Text(
                    state.nextCase?.title ?: "¡Has explorado todos los casos disponibles!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    "${state.casesAvailable} de ${state.casesTotal} casos desbloqueados",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun HomeNodeCard(node: HomeNode, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.height(150.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp).fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                ModuleIconArt(icon = node.icon, tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Spacer(Modifier.height(8.dp))
            Text(node.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 2)
            Spacer(Modifier.height(2.dp))
            Text(node.teaser, style = MaterialTheme.typography.bodyMedium, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
