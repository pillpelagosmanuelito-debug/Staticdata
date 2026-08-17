package com.educalab.staticdata.ui.screens.profileselect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.ui.illustration.AvatarArt
import com.educalab.staticdata.ui.illustration.DatiMascot
import com.educalab.staticdata.ui.illustration.MascotMood
import com.educalab.staticdata.util.LocalAppContainer
import com.educalab.staticdata.util.rememberAppViewModel

@Composable
fun ProfileSelectScreen(onSelect: (Long) -> Unit, onCreateNew: () -> Unit) {
    val container = LocalAppContainer.current
    val viewModel = rememberAppViewModel { ProfileSelectViewModel(container.profileRepository) }
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        DatiMascot(mood = MascotMood.HAPPY)
        Spacer(Modifier.height(12.dp))
        Text(
            "¿Quién va a investigar hoy?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))

        if (state.loading) return@Column

        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f)) {
            items(state.profiles) { profile ->
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onSelect(profile.id) }
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AvatarArt(avatarId = profile.avatarId, modifier = Modifier.size(72.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(profile.alias, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { onCreateNew() }
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.Add, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Nuevo detective", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
