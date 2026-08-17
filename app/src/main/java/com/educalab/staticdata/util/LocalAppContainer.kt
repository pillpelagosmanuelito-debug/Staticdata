package com.educalab.staticdata.util

import androidx.compose.runtime.compositionLocalOf

val LocalAppContainer = compositionLocalOf<AppContainer> {
    error("AppContainer no proporcionado: envuelve el árbol de Composables con CompositionLocalProvider.")
}

/** Id del perfil actualmente abierto en este proceso. El dispositivo puede tener
 *  varios perfiles (varias "cuentas" locales); este objeto solo recuerda cuál
 *  está activo mientras la app está en memoria — la elección persistente vive en
 *  [AppContainer.activeProfileId]. */
object CurrentUser {
    @Volatile var id: Long = 0L
}
