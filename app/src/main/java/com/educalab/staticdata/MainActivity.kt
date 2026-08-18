package com.educalab.staticdata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.educalab.staticdata.ui.navigation.StaticdataNavGraph
import com.educalab.staticdata.ui.theme.StaticdataTheme
import com.educalab.staticdata.util.LocalAppContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as StaticdataApp).container

        setContent {
            CompositionLocalProvider(LocalAppContainer provides container) {
                StaticdataTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        // El fondo del Surface sí llega borde a borde (pantalla completa
                        // moderna), pero el contenido interactivo se reserva fuera de la
                        // barra de estado, el notch y la barra de gestos para que nada
                        // quede tapado en ningún dispositivo.
                        StaticdataNavGraph(modifier = Modifier.fillMaxSize().safeDrawingPadding())
                    }
                }
            }
        }
    }
}
