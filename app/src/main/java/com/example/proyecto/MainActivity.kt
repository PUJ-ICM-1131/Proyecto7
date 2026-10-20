package com.example.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.proyecto.navigation.Nav3Host
import com.example.proyecto.ui.theme.ProyectoTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar osmdroid ANTES de crear cualquier MapView
        Configuration.getInstance().apply {
            load(
                applicationContext,
                getSharedPreferences("osmdroid_map", MODE_PRIVATE)
            )
            // Debe ir DESPUÉS de load(), y ser único y descriptivo
            userAgentValue = "ProyectoColombiaApp/1.0 (richinavas2007gmail.com)"
        }

        enableEdgeToEdge()
        setContent {
            ProyectoTheme {
                Nav3Host()
            }
        }
    }
}