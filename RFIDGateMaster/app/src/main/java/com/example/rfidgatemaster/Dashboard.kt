package com.example.rfidgatemaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Dashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✔ CORRECTO: Obtener rol desde SharedPreferences
        val prefs = getSharedPreferences("usuario", MODE_PRIVATE)
        val rol = prefs.getString("rol", "OPERADOR") ?: "OPERADOR"

        // Botones
        val btnGestionUsuarios = findViewById<Button>(R.id.btnUsuarios)
        val btnGestionSensores = findViewById<Button>(R.id.btnSensores)
        val btnHistorial = findViewById<Button>(R.id.btnHistorial)
        val btnControlBarrera = findViewById<Button>(R.id.btnBarrera)
        val btnPerfil = findViewById<Button>(R.id.btnPerfil)

        // 🔐 Si es operador → ocultar acceso admin
        if (rol == "OPERADOR") {
            btnGestionUsuarios.visibility = View.GONE
            btnGestionSensores.visibility = View.GONE
        }

        // Navegación
        btnGestionUsuarios.setOnClickListener {
            startActivity(Intent(this, GestionUsuarios::class.java))
        }

        btnGestionSensores.setOnClickListener {
            startActivity(Intent(this, GestionSensores::class.java))
        }

        btnHistorial.setOnClickListener {
            startActivity(Intent(this, Historial::class.java))
        }

        btnControlBarrera.setOnClickListener {
            startActivity(Intent(this, ControlBarrera::class.java))
        }

        btnPerfil.setOnClickListener {
            val intent = Intent(this, Perfil::class.java)
            startActivity(intent)
        }
    }
}
