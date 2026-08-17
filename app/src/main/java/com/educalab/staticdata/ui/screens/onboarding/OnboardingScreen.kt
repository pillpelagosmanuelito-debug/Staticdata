package com.educalab.staticdata.ui.screens.onboarding

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
import com.educalab.staticdata.ui.illustration.AVATAR_COUNT
import com.educalab.staticdata.ui.illustration.AvatarArt
import com.educalab.staticdata.ui.illustration.DatiMascot
import com.educalab.staticdata.ui.illustration.MascotMood
import com.educalab.staticdata.util.LocalAppContainer
import com.educalab.staticdata.util.rememberAppViewModel

private data class OnboardPage(val title: String, val body: String, val mood: MascotMood)

private val PAGES = listOf(
    OnboardPage(
        "Bienvenido a la Agencia de Detectives de Datos",
        "Cada día llegan casos nuevos que solo se resuelven mirando bien los datos. Tú serás quien los investigue.",
        MascotMood.HAPPY
    ),
    OnboardPage(
        "Soy Dati, tu dron analista",
        "Te acompañaré en cada caso: te contaré la misión, te daré pistas si te atascas y celebraré contigo cada acierto.",
        MascotMood.NEUTRAL
    ),
    OnboardPage(
        "Así se avanza en la Agencia",
        "Resuelve retos cortos, gana XP y sube de nivel para desbloquear nuevos casos, insignias y el laboratorio de muestras.",
        MascotMood.CELEBRATING
    ),
    OnboardPage(
        "Todo se queda en tu dispositivo",
        "Staticdata funciona sin internet. No pedimos tu nombre real ni datos personales: solo un alias y un avatar.",
        MascotMood.THINKING
    ),
)

@Composable
fun OnboardingScreen(onFinished: (profileId: Long) -> Unit) {
    val container = LocalAppContainer.current
    val viewModel = rememberAppViewModel { OnboardingViewModel(container.profileRepository) }

    var pageIndex by remember { mutableIntStateOf(0) }
    var alias by remember { mutableStateOf("") }
    var avatarId by remember { mutableIntStateOf(0) }
    val showProfileStep = pageIndex == PAGES.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        if (!showProfileStep) {
            val page = PAGES[pageIndex]
            DatiMascot(mood = page.mood)
            Spacer(Modifier.height(20.dp))
            Text(page.title, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text(page.body, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1f))
            Row {
                repeat(PAGES.size + 1) { i ->
                    Box(
                        Modifier
                            .padding(4.dp)
                            .size(if (i == pageIndex) 10.dp else 7.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (i == pageIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = { pageIndex++ }, modifier = Modifier.fillMaxWidth()) {
                Text(if (pageIndex == PAGES.size - 1) "Elegir mi alias" else "Siguiente")
            }
        } else {
            Text("Crea tu identidad de detective", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
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
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { viewModel.finish(alias, avatarId, onFinished) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Entrar a la Agencia") }
        }
    }
}
