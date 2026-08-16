package com.educalab.staticdata.util

import androidx.compose.runtime.compositionLocalOf

val LocalAppContainer = compositionLocalOf<AppContainer> {
    error("AppContainer no proporcionado: envuelve el árbol de Composables con CompositionLocalProvider.")
}

/** Id del perfil local activo. Al ser una app de un único jugador por dispositivo,
 *  siempre existe como máximo un UserProfile; se cachea aquí tras crearlo. */
object CurrentUser {
    @Volatile var id: Long = 0L
}
