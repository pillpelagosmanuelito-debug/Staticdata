package com.educalab.staticdata.ui.screens.profileselect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.ui.components.AgencyTopBar
import com.educalab.staticdata.ui.illustration.AVATAR_COUNT
import com.educalab.staticdata.ui.illustration.AvatarArt
import com.educalab.staticdata.util.LocalAppContainer
import com.educalab.staticdata.util.rememberAppViewModel

@Composable
fun CreateProfileScreen(onBack: () -> Unit, onCreated: (profileId: Long) -> Unit) {
    val container = LocalAppContainer.current
    val viewModel = rememberAppViewModel { CreateProfileViewModel(container.profileRepository) }

    var alias by remember { mutableStateOf("") }
    var avatarId by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AgencyTopBar(title = "Nuevo detective", onBack = onBack)
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Crea tu identidad de detective",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            AvatarArt(avatarId = avatarId)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = alias,
                onValueChange = { if (it.length <= 18) alias = it },
                label = { Text("Tu alias (no uses tu nombre real)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Text("Elige tu avatar", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.height(180.dp)) {
                items(AVATAR_COUNT) { id ->
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (id == avatarId) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { avatarId = id }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AvatarArt(avatarId = id)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { viewModel.create(alias, avatarId, onCreated) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Entrar a la Agencia") }
        }
    }
}
