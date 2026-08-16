package com.educalab.staticdata.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.domain.logic.ProgressRules
import com.educalab.staticdata.ui.components.AgencyTopBar
import com.educalab.staticdata.ui.components.SectionLabel
import com.educalab.staticdata.ui.components.XpProgressBar
import com.educalab.staticdata.ui.illustration.BadgeArt
import com.educalab.staticdata.util.LocalAppContainer
import com.educalab.staticdata.util.rememberAppViewModel

@Composable
fun ProgressScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val viewModel = rememberAppViewModel { ProgressViewModel(container.profileRepository, container.caseRepository) }
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AgencyTopBar(title = "Progreso y colección", onBack = onBack)

        Column(modifier = Modifier.padding(16.dp)) {
            XpProgressBar(
                currentXp = state.progress.totalXp,
                xpToNext = ProgressRules.xpForNextLevel(state.progress.totalXp),
                level = state.progress.level
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                StatBlock("Casos", "${state.progress.casesCompleted}/${state.totalCases}")
                StatBlock("Ejercicios", "${state.progress.exercisesCompleted}")
                StatBlock("A la 1ª", "${state.progress.exercisesCorrectFirstTry}")
                StatBlock("XP total", "${state.progress.totalXp}")
            }
            Spacer(Modifier.height(20.dp))
            SectionLabel("Colección de insignias (${state.unlockedIds.size}/${state.badges.size})")
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(state.badges) { badge ->
                val unlocked = badge.id in state.unlockedIds
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    BadgeArt(code = badge.code, unlocked = unlocked)
                    Spacer(Modifier.height(4.dp))
                    Text(badge.title, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, maxLines = 2)
                    if (!unlocked) {
                        Text(badge.description, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBlock(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}
