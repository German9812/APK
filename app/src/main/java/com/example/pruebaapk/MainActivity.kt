package com.example.pruebaapk

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicia la actividad de inicio de sesión al abrir MainActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Finaliza MainActivity para que no se pueda volver a ella con el botón "Atrás"
    }
}
