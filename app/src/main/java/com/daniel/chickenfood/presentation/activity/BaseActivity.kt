package com.daniel.chickenfood.presentation.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Habilita el diseño de borde a borde para que la app use toda la pantalla
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }
}