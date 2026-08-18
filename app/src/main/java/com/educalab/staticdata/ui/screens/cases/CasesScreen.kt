package com.educalab.staticdata.ui.screens.cases

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.domain.model.CaseFile
import com.educalab.staticdata.domain.model.CaseStatus
import com.educalab.staticdata.ui.components.AgencyTopBar
import com.educalab.staticdata.ui.components.ModuleHeroBanner
import com.educalab.staticdata.ui.components.StatusChip
import com.educalab.staticdata.ui.illustration.ModuleIcon
import com.educalab.staticdata.ui.illustration.ModuleIconArt
import com.educalab.staticdata.ui.theme.AmberStamp
import com.educalab.staticdata.ui.theme.CoralAlert
import com.educalab.staticdata.ui.theme.LimeZest
import com.educalab.staticdata.ui.theme.PinkPop
import com.educalab.staticdata.ui.theme.SkyBlue
import com.educalab.staticdata.ui.theme.TealClue
import com.educalab.staticdata.ui.theme.VioletMystery
import com.educalab.staticdata.util.LocalAppContainer
import com.educalab.staticdata.util.rememberAppViewModel

private val CATEGORY_ACCENTS = mapOf(
    "Frutas" to CoralAlert,
    "Mascotas" to SkyBlue,
    "Deportes" to LimeZest,
    "Libros" to VioletMystery,
    "Transportes" to AmberStamp,
    "Mediciones" to TealClue
)
private fun categoryAccent(category: String): Color = CATEGORY_ACCENTS[category] ?: PinkPop

@Composable
fun CasesScreen(onBack: () -> Unit, onOpenCase: (Long) -> Unit) {
    val container = LocalAppContainer.current
    val viewModel = rememberAppViewModel { CasesViewModel(container.caseRepository) }
    val cases by viewModel.cases.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AgencyTopBar(title = "Oficina de casos", onBack = onBack)
        ModuleHeroBanner(
            icon = ModuleIcon.FOLDER_MAGNIFIER,
            accent = CoralAlert,
            message = "Cada expediente es un caso real que se resuelve leyendo datos, no adivinando.",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(cases) { case -> CaseCard(case = case, onClick = { if (case.status != CaseStatus.BLOQUEADO) onOpenCase(case.id) }) }
        }
    }
}

@Composable
private fun CaseCard(case: CaseFile, onClick: () -> Unit) {
    val locked = case.status == CaseStatus.BLOQUEADO
    val accent = categoryAccent(case.category)
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.14f)),
        modifier = Modifier.fillMaxWidth().alpha(if (locked) 0.55f else 1f)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(accent.copy(alpha = 0.32f)),
                contentAlignment = Alignment.Center
            ) {
                ModuleIconArt(icon = ModuleIcon.CASE_STAMP, tint = accent)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(case.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(case.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 2)
                Spacer(Modifier.height(6.dp))
                StatusChip(status = case.status)
            }
            if (locked) {
                Text("Nivel ${case.minLevel}", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
