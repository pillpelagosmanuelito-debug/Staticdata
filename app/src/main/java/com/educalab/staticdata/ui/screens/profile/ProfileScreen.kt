package com.educalab.staticdata.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.ui.components.AgencyTopBar
import com.educalab.staticdata.ui.illustration.AVATAR_COUNT
import com.educalab.staticdata.ui.illustration.AvatarArt
import com.educalab.staticdata.util.LocalAppContainer
import com.educalab.staticdata.util.rememberAppViewModel

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val viewModel = rememberAppViewModel { ProfileViewModel(container.profileRepository) }
    val profile by viewModel.profile.collectAsState()

    var alias by remember(profile?.id) { mutableStateOf(profile?.alias ?: "") }
    var avatarId by remember(profile?.id) { mutableIntStateOf(profile?.avatarId ?: 0) }
    var soundEnabled by remember(profile?.id) { mutableStateOf(profile?.soundEnabled ?: true) }
    var hapticsEnabled by remember(profile?.id) { mutableStateOf(profile?.hapticsEnabled ?: true) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AgencyTopBar(title = "Mi perfil de detective", onBack = onBack)
        Column(modifier = Modifier.padding(20.dp)) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                AvatarArt(avatarId = avatarId, modifier = Modifier.size(110.dp))
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = alias,
                onValueChange = { if (it.length <= 18) alias = it },
                label = { Text("Alias") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Text("Cambiar avatar", style = MaterialTheme.typography.titleMedium)
            LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.height(180.dp)) {
                items(AVATAR_COUNT) { id ->
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (id == avatarId) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { avatarId = id }
                            .padding(4.dp)
                    ) { AvatarArt(avatarId = id) }
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { viewModel.updateAlias(alias, avatarId) }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar cambios")
            }

            Spacer(Modifier.height(24.dp))
            Text("Preferencias", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Text("Sonido de la app", modifier = Modifier.weight(1f))
                Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it; viewModel.setSoundEnabled(it) })
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Text("Vibración (háptica)", modifier = Modifier.weight(1f))
                Switch(checked = hapticsEnabled, onCheckedChange = { hapticsEnabled = it; viewModel.setHapticsEnabled(it) })
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Staticdata no guarda tu nombre real ni ningún dato personal: solo este alias y avatar, en tu propio dispositivo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
