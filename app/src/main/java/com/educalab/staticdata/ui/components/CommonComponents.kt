package com.educalab.staticdata.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.staticdata.domain.model.CaseStatus
import com.educalab.staticdata.ui.theme.ErrorRed
import com.educalab.staticdata.ui.theme.SuccessGreen

@Composable
fun AgencyTopBar(title: String, onBack: (() -> Unit)? = null, trailing: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Volver") }
        } else {
            Spacer(Modifier.width(12.dp))
        }
        Text(title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
        trailing?.invoke()
    }
}

@Composable
fun XpProgressBar(currentXp: Int, xpToNext: Int?, level: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Nivel $level", style = MaterialTheme.typography.labelLarge)
            Text(
                if (xpToNext != null) "$xpToNext XP para subir" else "¡Nivel máximo!",
                style = MaterialTheme.typography.labelMedium
            )
        }
        Spacer(Modifier.height(4.dp))
        val fraction = if (xpToNext == null) 1f else {
            val span = (xpToNext + (currentXp % 100).coerceAtLeast(1)).coerceAtLeast(1)
            (1f - xpToNext.toFloat() / span.toFloat()).coerceIn(0.05f, 1f)
        }
        val animated by animateFloatAsState(targetValue = fraction, animationSpec = tween(500), label = "xp")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun StatusChip(status: CaseStatus, modifier: Modifier = Modifier) {
    val (label, icon, color) = when (status) {
        CaseStatus.BLOQUEADO -> Triple("Bloqueado", Icons.Filled.Lock, Color(0xFF8A8F9C))
        CaseStatus.DISPONIBLE -> Triple("Disponible", Icons.Filled.PlayArrow, MaterialTheme.colorScheme.secondary)
        CaseStatus.INICIADO -> Triple("Iniciado", Icons.Filled.PlayArrow, MaterialTheme.colorScheme.primary)
        CaseStatus.COMPLETADO -> Triple("Completado", Icons.Filled.CheckCircle, SuccessGreen)
        CaseStatus.DOMINADO -> Triple("Dominado", Icons.Filled.WorkspacePremium, com.educalab.staticdata.ui.theme.BadgeGold)
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

@Composable
fun StarRow(count: Int, max: Int = 3, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        repeat(max) { i ->
            Icon(
                Icons.Filled.Star, contentDescription = null,
                tint = if (i < count) com.educalab.staticdata.ui.theme.BadgeGold else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/** Panel de feedback educativo tras responder: nunca solo "Correcto/Incorrecto". */
@Composable
fun FeedbackPanel(correct: Boolean, explanation: String, onContinue: () -> Unit, modifier: Modifier = Modifier) {
    AnimatedVisibility(visible = true, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (correct) SuccessGreen.copy(alpha = 0.14f) else ErrorRed.copy(alpha = 0.12f))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (correct) Icons.Filled.CheckCircle else Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (correct) SuccessGreen else ErrorRed
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (correct) "¡Bien resuelto, detective!" else "Casi lo tienes. Vamos a revisarlo.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (correct) SuccessGreen else ErrorRed
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(explanation, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = if (correct) SuccessGreen else MaterialTheme.colorScheme.primary)
            ) {
                Text(if (correct) "Continuar" else "Reintentar / Siguiente")
            }
        }
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(bottom = 4.dp)
    )
}
