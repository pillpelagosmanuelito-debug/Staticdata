package com.educalab.staticdata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
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
                        StaticdataNavGraph()
                    }
                }
            }
        }
    }
}
